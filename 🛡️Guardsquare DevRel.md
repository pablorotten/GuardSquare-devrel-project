Offer: 

* [https://www.linkedin.com/jobs/view/4160833895/](https://www.linkedin.com/jobs/view/4160833895/)   
* [https://job-boards.greenhouse.io/guardsquare/jobs/6575758](https://job-boards.greenhouse.io/guardsquare/jobs/6575758) 

# Low-Level Hacker with ProGuard open source tools

## 1. JADX vs proguard-assembler

Solve Android UnCrackable L1 with *JADX* and *proguard-assembler*

Download from here: https://mas.owasp.org/crackmes/

Videos of people that already do this:

### 1.1 UnCrackable with JADX

We are going to convert the `apk` into readable `java` code

* Open the apk with JADX: https://github.com/skylot/jadx
* Show `MainActivity` and `a` classes and extract the relevant code
* Create new project with relevant classes and generate the password
* Test the password in the phone

### 1.2 UnCrackable with proguard-assembler

We are going to convert the `apk` into not so readable but more powerful `jbc` code

#### Setup *proguard-assembler*

Download [proguard-assembler](https://github.com/Guardsquare/proguard-assembler/tree/master) from github, unzip the project and enter in the folder
```sh
cd proguard-assembler-master
```
Use jdk 21 (my specific case using windows and scoop)
```sh
scoop reset temurin21-jdk
```
Build the project 
```sh
.\gradlew clean build
```
Get the generated `assembler.jar`
```
proguard-assembler-master\pga-cli\build\libs\assembler.jar
```

> [!TIP]
> `assembler.jar` is the tool we need to use here
> For the future, only this file is needed to run *proguard-assembler*

#### Prepare the apk for `assembler.jar`

*proguard-assembler* can't process an apk directly. It needs the `.jar` with all the `.class` inside to generate the `.jbc` files

1. Unzip `UnCrackable-Level1.apk`. It doesn't contain any `.jar` or `.class` files but it has `classes.dex`.
2. Download [dex2jar](https://github.com/pxb1988/dex2jar/releases) and unzip  
3. Run it against `classes.dex`  
```sh
.\d2j-dex2jar.bat classes.dex
```

> [!TIP]
> This generates `classes-dex2jar.jar` with all the  `.class` files.

 Now it's ready to be used with *proguard-assembler*

#### Run *proguard-assembler* and solve the challenge

```bat
java -jar .\assembler.jar .\classes-dex2jar.jar .\assembler-output
```

This will decompile the app into `jbc` (Java ByteCode) files.

Taking a look to those `jbc` files we can figure out what the app does:

**MainActivity.jbc**
```
public void verify(android.view.View) {
  ...
  invokestatic sg.vantagepoint.uncrackable1.a#boolean a(java.lang.String)
  ifeq label1
  aload_2
  ldc "Success!"
  invokevirtual android.app.AlertDialog#void setTitle(java.lang.CharSequence)
  ldc "This is the correct secret."
```

Here we can see that there's a comparison (`uncrackable1.a#boolean `) between 2 values

**uncrackable1.a**
```
public class sg.vantagepoint.uncrackable1.a extends java.lang.Object {

    public static boolean a(java.lang.String) {
            ldc "5UJiFctbmgbDoLXmpL12mkno8HT4Lv8dlat8FxR2GOc="
            iconst_0
            invokestatic android.util.Base64#byte[] decode(java.lang.String,int)
            astore_1
        label2:
            ldc "8d127684cbc37c17616d806cf50473cc`"
            invokestatic #byte8d127684cbc37c17616d806cf50473cc[] b(java.lang.String)
            aload_1
            invokestatic sg.vantagepoint.a.a#byte[] a(byte[],byte[])
            ...
```
Here we can find 2 suspicious strings and a method call with tem:
* `5UJiFctbmgbDoLXmpL12mkno8HT4Lv8dlat8FxR2GOc=`. A `String` that is decoded with `Base64` to `byte`
* `8d127684cbc37c17616d806cf50473cc` An Hexadecimal that is converted with `b()` to `byte`
* Method sg.vantagepoint.a.a that asks for 2 `byte`

**vantagepoint.a**
```
public class sg.vantagepoint.a.a extends java.lang.Object {

    public static byte[] a(byte[], byte[]) {
            new javax.crypto.spec.SecretKeySpec
            dup
            aload_0
            ldc "AES/ECB/PKCS7Padding"
            invokespecial javax.crypto.spec.SecretKeySpec#void <init>(byte[],java.lang.String)
            astore_0
            ldc "AES"
            invokestatic javax.crypto.Cipher#javax.crypto.Cipher getInstance(java.lang.String)
```

Here's the dought 🍪!
This method is using `AES` encryption with `ECB` mode and `PKCS7Padding`. The first parameter is the key and the second parameter is the data to decrypt.

So we can conclude that:
* Password to decrypt: `5UJiFctbmgbDoLXmpL12mkno8HT4Lv8dlat8FxR2GOc=` converted to `byte` with `Base64` decoding
* AES key: `8d127684cbc37c17616d806cf50473cc` converted to `byte` from Hexadecimal
* Decryption method: `AES/ECB/PKCS7Padding`

We can go to [CyberChef](https://gchq.github.io/CyberChef/#recipe=From_Base64('A-Za-z0-9%2B/%3D',false,true)AES_Decrypt(%7B'option':'Hex','string':'8d127684cbc37c17616d806cf50473cc'%7D,%7B'option':'Hex','string':''%7D,'ECB','Raw','Raw',%7B'option':'Hex','string':''%7D,%7B'option':'Hex','string':''%7D)&input=NVVKaUZjdGJtZ2JEb0xYbXBMMTJta25vOEhUNEx2OGRsYXQ4RnhSMkdPYz0) and do the decryption:

![alt text](images/cyberchef.png)

### 1.3 Conclusion

#### How to fix this

* String Encryption (DexGuard/ProGuard): This would encrypt the key itself and only decrypt it in memory for a split second when needed. In your .jbc file, you wouldn't see 8d12..., you would see a scrambled mess that only unscrambles at runtime.

* Android Keystore: Instead of putting the key in the code, the app should generate a key inside the phone's dedicated security chip (the TEE - Trusted Execution Environment). The code never actually "sees" the key; it just asks the chip to "please decrypt this for me."

* Native Code (C/C++): Moving the key into a .so file (JNI) makes it slightly harder to find than in a standard .java or .jbc file, though a determined hacker can still find it using tools like Ghidra.

#### JADX vs proguard-assembler

⚠⚠⚠ WIP ⚠⚠⚠
Explain why with JADX is easier but with proguard-assembler is more accurate
⚠⚠⚠ WIP ⚠⚠⚠

## 2. Modify the app with proguard-assembler

Let's do something next level. Let's directly modify the apk so no matter what string user inputs, it will always suceed

## 3. Proguard/R8 vs Dexguard

* Why is not Proguard/R8 enought?
* How can theoretically Dexguard help us??

# ☄️ Other Ideas for devrel portfolio

* Hack with Apktool + Smali
* Hack with Frida
* Hack with Ghidra / IDA Pro
  
## ❌ Discarded ideas:

**Hacking a vanilla APK vs Proguard APK:** 

* Find an open source android app. compile it to APK and then do reverse code hacking with jadx Do the same but now use proguard and explain differences in a video.  
* **❌ Problem:** Nobody uses proguard anymore, now Android provides a built-in tool called **R8** that does the same

**Dexguard tutorial**

* Since R8 became a thing now Guardsquare main product is Dexguard. Investigate and make a video about it  
* **❌ Problem:** It’s paid 💵 and expensive. So I can’t use it

**Using AppSweep with a hackable app:**

* Hack OWASP MASTG [Android UnCrackable L](https://mas.owasp.org/crackmes/Android#android-uncrackable-l1) and solve the problem. Then use AppSweep with the apk and check if it finds the issue. Make a video about that  
* **❌ Problem:**It’s paid 💵and expensive. So I can’t use it. Mailed them to ask for a trial version but they said no.

## 📽️ Relevant videos

Proguard explanations:

* [https://www.youtube.com/watch?v=q4HQ0H-IQSU](https://www.youtube.com/watch?v=q4HQ0H-IQSU)   
* [https://www.youtube.com/watch?v=F9ymcWoDEtc](https://www.youtube.com/watch?v=F9ymcWoDEtc)   
* [https://www.youtube.com/watch?v=M0fMzgBcACY](https://www.youtube.com/watch?v=M0fMzgBcACY)   
* [https://youtu.be/x9T5EYE-QWQ?si=9e4tkuQKrWsxqTpx](https://youtu.be/x9T5EYE-QWQ?si=9e4tkuQKrWsxqTpx)   
* [https://www.youtube.com/watch?v=DZHn17qg5\_Y](https://www.youtube.com/watch?v=DZHn17qg5_Y)   
* [https://www.youtube.com/watch?v=vUBI-L1602s](https://www.youtube.com/watch?v=vUBI-L1602s) 

Hack APK:

* [https://www.youtube.com/watch?v=7kKl3nokZso](https://www.youtube.com/watch?v=7kKl3nokZso)   
* [https://www.youtube.com/watch?v=JyaGLDM8Xhk](https://www.youtube.com/watch?v=JyaGLDM8Xhk)   
* **Jadx** [https://www.youtube.com/watch?v=QlpDMmfOUmM](https://www.youtube.com/watch?v=QlpDMmfOUmM) 
* Hacking Block Blast: https://www.youtube.com/watch?v=y4tMta9w6o0

JADX

* Good video:
  * [https://www.youtube.com/watch?v=F7CoOjjlR9U](https://www.youtube.com/watch?v=F7CoOjjlR9U)
* Bad videos:
  * [https://www.youtube.com/watch?v=FvJVtPfEJM4](https://www.youtube.com/watch?v=FvJVtPfEJM4)   
  * [https://www.youtube.com/watch?v=R3\_KUP02mXk](https://www.youtube.com/watch?v=R3_KUP02mXk)   
  * [https://www.youtube.com/watch?v=P6rNPkM2DdY](https://www.youtube.com/watch?v=P6rNPkM2DdY)   
  * [https://www.youtube.com/watch?v=9VjkPuzSJNo](https://www.youtube.com/watch?v=9VjkPuzSJNo)   
  * [https://www.youtube.com/watch?v=xRQVljerl0A](https://www.youtube.com/watch?v=xRQVljerl0A)


# 🎓 What I learned

##  Intermediate Representation, or IR

modern compilers like **Clang/LLVM** actually use a "temporary bytecode" internally during the building process.  
* **Front-End (Clang):** Turns your C++ into **LLVM Bitcode** (Intermediate Representation)
* **Optimizer:** Polishes that Bitcode to make it fast.  
* **Back-End:** Finally turns that Bitcode into the **Machine Code** (.exe or ELF).  
* **Crucially:** Usually, the developer "throws away" the Bitcode and only ships the Machine Code. Tools like **LibEBC** are for cases where the developer *chose* to embed that "recipe" inside the binary (common in Apple's ecosystem).

## Embedded Bitcode (EBC):

When developers compile code for iOS (or sometimes Android native libraries), they don't always just produce the final machine code (the binary the phone runs). They can also embed LLVM Bitcode.  
* Bitcode is an intermediate representation (**IR**)—it's halfway between source code and machine code.  
* The Apple Use Case: Apple asks developers to upload Bitcode to the App Store so that Apple can re-compile and optimize the app for new iPhones without the developer needing to upload a new version.

## Scoop 

handle different sofrtware versions in Windows. Useful for jdk versions
  * Install: 
```sh
iwr -useb get.scoop.sh | iex

scoop bucket add java   
scoop install openjdk8-redhat   
scoop install openjdk25  
```
  * Usage: 
```sh
scoop reset openjdk8-redhat
scoop reset openjdk25
```

## JADX

This converts `apk` --> `java` code

https://github.com/skylot/jadx
A Dex to Java decompiler. Produces Java source code from Android Dex and Apk files 

## ProGuard Assembler

This converts  `.class` (could be a `jar` with multiple `.class` files) --> `jbc` and the opposite

https://github.com/Guardsquare/proguard-assembler
The disassembler can do 2 different things:
* take `class` files and convert them to readable `jbc` (Java ByteCode) files, following the [ProGuard Assembly Language specification](https://github.com/Guardsquare/proguard-assembler/blob/master/docs/md/specification.md).
* take readable `jbc` files and convert them to `class` files.

## JADX vs ProGuard Assembler

Decompilers like JADX are **guessing**. They try to reconstruct Java from bytecode, but if the code is heavily obfuscated (using techniques like _Control Flow Flattening_), JADX often fails, shows "/* error */", or produces misleading code
- **JADX:** A high-level interpretation (can be wrong) but if succeeds it produces `.java` human readable files
- **Assembler (.jbc):** A 1:1 representation of what the phone actually executes. It cannot be "broken" by obfuscation because it's just a list of raw instructions. But it's less human readable.

## classes.dex

While standard Java compiles into .class files, Android uses its own optimized format for it's virtuakl (bytecode for the Dalvik or ART runtime).

## dex2jar

https://github.com/pxb1988/dex2jar/wiki/Faq

Tool for converting Android's `.dex` format to Java's `.class` forma

## Decomplie a .class to bytecode with javap

```sh
javap -c -classpath .\UnCrackable-Level1-classes-dex2jar.jar sg.vantagepoint.uncrackable1.MainActivity > MainActivity.jbc
```

> [!IMPORTANT]  
> TODO: Explain differences with proguard assembler: "Here is the breakdown of why the ProGuard Assembler output is "better" for your specific hacking and reconstruction goal..."


## Apktool + Smali

The Industry Standard for Patching.

If a hacker wanted to do exactly what you are doing—rebuild and resign the APK—they would almost certainly use Apktool.

How it works: Apktool decompiles the APK into Smali files. Smali is the "standard" assembly language for Android's Dalvik/ART virtual machine.

Why it's preferred: It's universal. Every Android app has Smali, but not every app was built using ProGuard in a way that makes JBC (Java Bytecode) extraction easy. Most "cracked" apps you find on the internet were made by editing Smali code.

The Workflow: Apktool Decompile -> Edit .smali files -> Apktool Build -> uber-apk-signer.

## Frida

The Professional "Surgical" Tool.

A professional "Red Team" hacker often doesn't even bother rebuilding the APK. They use Frida, which is a dynamic instrumentation toolkit.

The "Magic" of Frida: Instead of changing the code on disk, Frida "injects" JavaScript into the app while it's running on the phone.

The Hack: A hacker would write 5 lines of JavaScript to say: "Whenever the method sg.vantagepoint.a.c.a() is called, don't actually run it—just return false (Not Rooted)."

Why it's better: It bypasses Signature Verification. Since the hacker never modified the APK file itself, the app's internal security checks won't notice that anything has changed.

## Ghidra / IDA Pro

The "Heavyweight" Analysis.

For apps with Native Code (.so files written in C++), hackers use these $10,000+ industry-standard tools (though Ghidra is free from the NSA). These are used when the "Secret Key" isn't in Java, but hidden deep in the machine code of the phone's processor.