# 🎬 Script of devrel video: Modifying Java ByteCode with ProGuard Assembler

| Section | Duration | Focus |
| --- | --- | --- |
| 1. Presentation | 0:45 | Branding & ProGuard ecosystem. |
| 2. Setup | 1:15 | Building the tool (shows you're a dev). |
| 3. Challenge | 1:00 | The "Problem" (The App's locked door). |
| 4. Disassembly | 0:45 | From .jar to .jbc. |
| 5. Analysis | 2:30 | The logic (Hex, Base64, AES, ifeq). |
| 6. Why JBC? | 0:45 | Precision vs. Readability. |
| 7. The Patch | 2:00 | Modifying and Resigning (The "Aha!" moment). |
| 8. Conclusion | 0:45 | Call to action & Guardsquare shoutout. |

## 1. Presentation

Hello!!! 
Today we are going to discover **ProGuard Assembler**. A **powerful** tool developed by **Guardsquare**, a leading company in mobile app security.

Guardsquare provide tools and services to protect mobile applications:

> Open Guardsquare website and show the products

Such as [...] and **ProGuard**: an open source Java shrinker, optimizer, obfuscator that it's a must in the Android development world.

If we take a look to the [GitHub page](https://github.com/Guardsquare)

> Open Guardsquarer GitHub page

We can find next to **ProGuard**, **Proguard Assembler**, the tool we will explore today.
 
## 2. Setup

**proguard-assembler** can take a `.jar` file, extract the `.class` it has inside; and disassemble them into readable `.jbc` (Java ByteCode) files. And then, it can do the opposite. From `jbc` files, it can assemble them back into `.class` files and put them in a `.jar`.

JBC is an internal format created by Guardsquare that represents Java bytecode in a human readable format. It is not an official format, but it is designed to be easy to read, understand and modify.
> Show a `.jbc` file to show how it looks like  

We find this tool in GitHub as a Gradle project. We have to download it and build it to get the `assembler.jar` file.
> Show the README file in the GitHub page to show how to build it
> ✂️ Download, unzip, and build the project with `./gradlew clean build` ✂️
> Show the `build/libs/assembler.jar` file

## 3. Challenge

Alright! Time to see **proguard-assembler** in action.

> Open OWASP MASTG website

This is the **OWASP Mobile Application Security** website. It provides a set of challenges to practice mobile app security testing. They are meant to be solved by reverse engineering the apps, so let's try one of them.

> ✂️ Download **UnCrackable L1** and install it in an Android emulator ✂️

I've installed the app in MeMu emulator. Let's open it to see what it does.
This app asks for a secret password. If we input a wrong password it shows an error message.

> Open the app and enter a random password to show the error message

**proguard-assembler** can't process an apk directly. It needs a `.jar` file.
> Show the usage in the README file

So we need to convert the apk into a jar file. We can do that with a tool called `dex2jar`.
> Show the dex2jar GitHub page, run the command and show the `classes-dex2jar.jar` file

## 4. Disassembly

Now, with `classes-dex2jar.jar` file, we can run the assembler to get all the `jbc`.

> run the command `java -jar ./assembler.jar ./classes-dex2jar.jar ./assembler-output` and open assembler-output folder with vscode to show the `jbc`

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

You might be thinking, why all this hustle? With other tools such as `jadx` or `Jeb` we could have not only decompiled the app, but also got the source code in a more readable format. So why would we want to use proguard-assembler?
This is because we haven't unleashed the full potential of proguard-assembler 😉

## 7. The Patch

Remember that ifeq condition in the `verify()` method? 
> Show the jbc code. The part of `ifeq` "Jump if 0 (False)."

What if, instead of trying to find the correct password, we just modify the code to always show "Success!"?
> Show how to modify the code to always show "Success!" and then run the assembler to get the modified jar file
> Show how to convert the modified jar file back to apk with `jar2dex` and sign it with `apksigner`
> Install the modified apk in the emulator and show that it always shows "Success!" regardless of the password we input

## 8. Conclusion

With proguard-assembler we can not only analyze the bytecode of an app, but also modify it to change its behavior. This tool can be used standalone, or as a library in our own projects to automate the process of modifying bytecode.

While we can patch this open-source challenge easily, this is exactly why enterprise tools like `DexGuard` use **App Integrity** to detect if the bytecode has been modified.

We also learned about `jbc` and now we can understand at what level Guardsuqree's tools work, and how they can be used to protect our apps from reverse engineering and tampering.