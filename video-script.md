## Video timing plan

| Section                        | Duration | Focus                                       |
| ------------------------------ | -------- | ------------------------------------------- |
| 1. Presentation                | 0:30     | Why mobile security matters + OWASP context |
| 2. Disassemble                 | 0:45     | Disassemble the app into JBC files          |
| 3. Patch the app + reassemble  | 0:30     | Remove root check and assemble back         |
| 4. Solve the challenge         | 4:00     | Find the secret and decrypt it              |
| 5. Conclusion                  | 0:45     | Show Guardsquare products                   |

**Total:** 6:30

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
* [ ] What is JBC code explanation do it when already hacking with code

## 1. Presentation

Have you ever wondered how hackers find vulnerabilities in mobile applications? 

> [Visual: Quick cut to news headline about a mobile data breach or the ReVanced/Spotify patchers we discussed.]

From unlocking premium features and bypassing license checks, to stealing user data or discovering if an app is spying on you—it all starts at the same level: the bytecode.

Today, I'm going to show you how to reverse engineer an Android application using ProGuard Assembler.
Proguard Assembler it's a powerful tool from Guardsquare, a leading company in mobile application security. It allows us to disassemble and modify Java bytecode. 

With that tool, we’ll modify the Bytecode, bypass a security check, and solve a professional security challenge.

---

The challenge we are going to solve is from the OWASP Mobile Application Security project.

> [!NOTE]
> Show the webpage: https://mas.owasp.org/crackmes/ 

They are meant to be solved by reverse  engineering, so let's try one of them.

> ✂️ Download **UnCrackable L1** and install it in an Android emulator ✂️

I've installed the app in this Android emulator. Let's open it to see what it does.

Ooops! it detects that the device is rooted and it closes immediately. 

The first step is to bypass this root check.

## 2. Dissasemble

To bypass this security check we saw in the app, the first step is to convert it into a human readable format that we can modify.

For doing that, we need to disassemble the app. Those are the steps we need to do:

- First we extract the `classes.dex` from the app and transform it into a `.jar` file with `dex2jar`. 
- Now, we can finally use **proguard-assembler** to disassemble the `.jar` file into readable `.jbc` files.
- Those jbc files are the Java ByteCode in a human readable format. This is what we can read and modify.

## 3. Patch the app + reassemble 

We've completed the dissasembling part, now we can start modifying the code to bypass the root check.

> [!NOTE]
> Show and explain what is JBC code.

This is the output folder. JBC is not as easy as Java code, but we can understand it.

- Remember that the app was closing with a message "Root detected!"?
- We have to find the piece of code that checks if the device is rooted and bypass it.
- We found it here, in `onCreate()` method of the `MainActivity` class.

> [!NOTE]
> Show were the root check is 

- If we delete this code, the app will not check if the device is rooted and it will not close immediately.

> [!NOTE]
> Show the modified code and explain that we just removed the root check.

- Apparently, we solved the first part of the challenge. Now we have to do the opposite of what we did before to get back the patched app.

This process is a bit complex, so I'll just summarize it:

* First, assemble the modified jbc code back to jar with **proguard-assembler**
* Then, we convert the jar into dex file with `jar2dex`
* Now, replace the original `classes.dex` in the apk
* Sign it with `uber-apk-signer`
* And finally, install it in the Android emulator.

> [!NOTE]
> Do all the steps in FF and in parallel

We are almost there! Now we can open the app again and see if it works.

and... yes! We can see the app and it doesn't detect root anymore. Now we can use the app without any problem.

> [!NOTE]
> Show that the app is working and doesn't detect root anymore.
> Input a random string and show that it is not correct.

### Patching app conclusion

We have successfully bypassed the root check of the app, by injecting a small patch in the bytecode. And then reassembling it back to install it in the phone.

This is a very common technique used by hackers. It's called **tampering**.

Tampering is an unauthorized modification of the application's code. 

Spotify go, Whatsapp plus, all the Revanced apps... those mods offer features that are not available in the original app, like downloading music for free, blocking ads, or unlocking premium features. 

But they are not official and you never know what code they have injected. They could also be stealing your data, spying on you, or even worse.

This is where tools like **DexGuard** and **iXGuard** come into play. 

They have features like **Anti-tampering** that causes the app to crash if it detects that the code has been modified. And **Polymorphism** making it much harder to find those checks and bypass them.

Another interesintg tool is **AppSweep**, that can help you to find vulnerabilities in your app before the hackers do.

## 4. Solve the challenge 

Now we can solve the challenge.

> [!NOTE]
> Open again the JBC code and show the strings converted to byte code
> Do the convertion in parallel using **CyberChef** 
> Show the decryption code
> Do in in **CyberChef** in parallel
> Once we have the secret, try in the app and show that it works and we get the "Success!" message.

## 5. Conclusion

if this is clearly a toy exercise , but the same strategy of reverse engineering can be used for more sophisticated attacks.

To protect against this kind of attacks, you can use Guardsquare products like **DexGuard** for Android and **iXGuard** for iOS. They have features like code obfuscation, string encryption, and tamper detection that can make it much harder for attackers to reverse engineer your app and find vulnerabilities.