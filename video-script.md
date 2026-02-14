# 🎬 Script of devrel video: Modifying Java ByteCode with ProGuard Assembler

| Section         | Duration | Focus                                        |
| --------------- | -------- | -------------------------------------------- |
| 1. Presentation | 0:30     | Branding & ProGuard ecosystem.               |
| 2. Setup        | 0:45     | Building the tool (shows you're a dev).      |
| 3. Challenge    | 0:50     | The "Problem" (The App's locked door).       |
| 4. Disassembly  | 0:15     | From .jar to .jbc.                           |
| 5. Analysis     | 4:00     | The logic (Hex, Base64, AES, ifeq).          |
| 6. Why JBC?     | 0:45     | Precision vs. Readability.                   |
| 7. The Patch    | 2:00     | Modifying and Resigning (The "Aha!" moment). |
| 8. Conclusion   | 0:45     | Call to action & Guardsquare shoutout.       |


# Feedback

* [ ] Record again the thank you scene
* [ ] Chapters in youtube with time stamps
* [ ] Present myself at the beginning of the video
* [ ] `dex2jar` part has to be 5 seconds
* [ ] The JBC code part is too complex for general audience but too simple for technical audience. Focus on beginners:
  * [ ] The part of modifying the code is a bit boring and pointless since we already solved the challenge:
    * Run the app with root activated and show that the app detects it and closes immediately. 
    * Modify the code to bypass the root detection and show that it works.
  * [ ] Solving the challenge:
    * Show the `ifeq` line that is used to jump to "Success!" or "Error!" message
    * Show that the secret is decoded from `Base64`
    * Show that the key is decoded from `hexadecimal`
    * Show that we use the key and the `AES/ECB/PKCS7Padding` cipher to decrypt the secret and compare with user's input
    * Meanwhile you see each of the key code lines, start building the recipe in parallel using **CyberChef**
    * Delete the part of reading the documentation of `Cipher.init()`
* [ ] I would start explaining why this is interesting, like why security in mobile applications is important and not always well implemented and how this owasp challenge is a little example. And in the end I'll retake this point adding that even if this is clearly a toy exercise the same strategy of reverse engineering can be used for more sophisticated attacks.
* [ ] Explain better the goal of the video. Talk about security in mobile applications 
* [ ] The "boring part" of converting from apk to jbc and the opposite explain it more visually with an animation.

## 1. Presentation

Hello!!! 
Today we are going to discover **ProGuard Assembler**. A **powerful** tool developed by **Guardsquare**, a leading company in mobile app security.

Guardsquare provide tools and services to protect mobile applications:

> Open Guardsquare website and show the products

Such as [...] and **ProGuard**.

If we take a look to the [GitHub page](https://github.com/Guardsquare)

> Open Guardsquarer GitHub page

We can find next to **ProGuard**, **Proguard Assembler**, the tool we will explore today.
 
## 2. Setup

**proguard-assembler** can take a `.jar` file, extract the `.class` it has inside; and disassemble them into readable `.jbc` (Java ByteCode) files. 

JBC is an internal format created by Guardsquare that represents a more human readable Java bytecode. It is not official, but it is designed to be easy to read, understand and modify.
> Show meanwhile `.jbc` file to show how it looks like in a slow scroll

Then, we can modify the JBC file, and with **proguard-assembler** do the opposite: Assemble them back into classes  and put them in a `.jar`

We find this tool in GitHub as a Gradle project. We have to download it and build it to get the `assembler.jar` file.
> ⏩ Build the project in FF ⏩

## 3. Challenge

Time to see **proguard-assembler** in action!

> Open OWASP MASTG website

We are going to solve one of the MAS Crackmes challenges. from **OWASP Mobile Application Security** website. They are meant to be solved by reverse engineering, so let's try one of them.

> ✂️ Download **UnCrackable L1** and install it in an Android emulator ✂️

I've installed the app in this Android emulator. Let's open it to see what it does.
It asks for a secret string. If we input a wrong one, it shows an error message.

> Open the app and enter a random password to show the error message

**proguard-assembler** can't process an apk. It needs a `.jar` file.

> Show the usage in the README file

We need to convert the apk into a jar. We can do that with a tool called `dex2jar`, that extracts the classes inside the apk and creates a `.jar` file
> Show the dex2jar GitHub page, run the command and show the `classes-dex2jar.jar` file

## 4. Disassembly

Now, with `classes-dex2jar.jar` file, we can run the assembler to get all the `jbc`.

> run the command `java -jar ./assembler.jar ./classes-dex2jar.jar ./assembler-output` and show the output folder is created.

## 5. Analysis

We are ready to solve the challenge. This part can be a bit technical, so I will try to keep it simple and not go into too much details.

1. First, we look into `MainActivity.jbc`. 
    1. In the method `verify()` we can see that there's a comparison between 2 values. 
    2. If the comparison is true it shows "Success!" otherwise it shows "Error!". 
    3. The boolean comparison is done by the method `sg.vantagepoint.uncrackable1.a#boolean a(java.lang.String)`. So we need to understand what this method does to find the correct password.

2. In the method `sg.vantagepoint.uncrackable1.a#boolean a(java.lang.String)`:
    1. We can find 2 suspicious strings. 
    2. The first one is converted into `byte` with `Base64` decoding
    3. The second one is converted into `byte` using a custom method `b()`. 
    4. The method `b()` is just a loop that converts every hexadecimal character pair into a byte. 
    5. Once we have those 2 `byte` arrays, they are passed to the method `sg.vantagepoint.a.a#byte[] a(byte[],byte[])`. Let's see what this method does.

3. The method `sg.vantagepoint.a.a#byte[] a(byte[],byte[])`:
    1. Takes 2 parameters, the first one, the one that was directly converted from hexadecimal to byte, is used as the key to create a `SecretKeySpec` using the algorithm "AES/ECB/PKCS7Padding".
    2. The second parameter, the one that was converted from Base64 to byte, is the data to decrypt.
    3. It uses the `Cipher` using `AES` algorithm and initializes it in decryption mode with the key. 
    4. Finally, it decrypts the data and returns the result.

## 6. Why JBC? 

So far, we have been able to disassemble the app and understand the JBC code to find the correct password. But we haven't unleashed the full potential of **proguard-assembler**. Remember that we can also modify the JBC code and assemble it back to a jar file.

## 7. The Patch

We're back into `MainActivity.jbc`

This is the `a()` method thata decrypts the password and compares with user's input. It returns a boolean, and then, the next  `ifeq` instruction takes it and determines if we see the "Success!" or the "Error!" message.

We just need to delete the `ifeq` and we will always see the "Success!" message !!!

> Show the jbc code. The part of `ifeq` "Jump if 0 (False)."

Then it comes the boring part:
* Assemble the modified jbc files back to jar with **proguard-assembler**
* Convert jar into dex file with `jar2dex`
* Replace the original `classes.dex` with the modified one
* Sign it with `uber-apk-signer`
* And finally, install it in the Android emulator.

> Do all the steps in FF and in parallel using 

## 8. Conclusion

We’ve just seen how to work directly at the bytecode level. 
This is the 'source of truth' where all Guardsquare tools operate.

This technology is the foundation of the open-source `ProGuard`, allowing it to analyze, shrink, and optimize Java code with total precision.

But it doesn't stop there. This same bytecode logic is what powers:

`DexGuard` & `iXGuard`: To obfuscate and encrypt your app,

`AppSweep`: To scan the bytecode for vulnerabilities like the one we just exploited.

## 9. Thanks for watching!

Thank you for watching this video! I hope you found it interesting and learned something new about Java bytecode.

If you want to learn more, check my GitHub repository where I have all the code and resources used in this video.
