
Offer: 

* [https://www.linkedin.com/jobs/view/4160833895/](https://www.linkedin.com/jobs/view/4160833895/)   
* [https://job-boards.greenhouse.io/guardsquare/jobs/6575758](https://job-boards.greenhouse.io/guardsquare/jobs/6575758) 

# 🪜 Steps

## The "Low-Level" Hacker (ProGuardCORE)

### ✅ 1\. Show the UnCrackable L1 code in JADX

Videos of people that already do this:

Cringe  videos doing the UnCrackable L1:

* [https://www.youtube.com/watch?v=FvJVtPfEJM4](https://www.youtube.com/watch?v=FvJVtPfEJM4)   
* [https://www.youtube.com/watch?v=R3\_KUP02mXk](https://www.youtube.com/watch?v=R3_KUP02mXk)   
* [https://www.youtube.com/watch?v=P6rNPkM2DdY](https://www.youtube.com/watch?v=P6rNPkM2DdY)   
* [https://www.youtube.com/watch?v=9VjkPuzSJNo](https://www.youtube.com/watch?v=9VjkPuzSJNo)   
* [https://www.youtube.com/watch?v=xRQVljerl0A](https://www.youtube.com/watch?v=xRQVljerl0A) 

Only good video:

* [https://www.youtube.com/watch?v=F7CoOjjlR9U](https://www.youtube.com/watch?v=F7CoOjjlR9U) 

### 2\. Use ProGuard Assembler to "Disassemble" the class file into .jbc (readable assembly)

1. Unzip `UnCrackable-Level1.apk`  
2. Convert `classes.dex` into actual classes with `dex2jar`  
	1. Download [https://github.com/pxb1988/dex2jar/releases](https://github.com/pxb1988/dex2jar/releases)  
	2. `.\d2j-dex2jar.bat classes.dex`  
	3. Generates `classes-dex2jar.jar`  
3. Setup `proguard-assembler` [https://repo1.maven.org/maven2/com/guardsquare/proguard-assembler/1.0.0/](https://repo1.maven.org/maven2/com/guardsquare/proguard-assembler/1.0.0/)   
	1. Build (use **jdk 8** with `scoop`): `./gradlew clean build
	2. Find the .bat file: .\pga-cli.bat disassemble:  `C:\Users\pablo\DEV\Apps\proguard-assembler-master\pga-cli\build\install\proguard-assembler\bin\pga-cli.bat`
4. Use the assembler
```
.\pga-cli.bat "C:\Users\pablo\DEV\projects\GuardSquare\apk-hacking\UnCrackable-L1\UnCrackable-Level1\classes-dex2jar.jar" -out "C:\Users\pablo\DEV\projects\GuardSquare\apk-hacking\UnCrackable-L1\UnCrackable-Level1\disassembled_output"
```

### 3\. Use **ProGuardCORE** to write a small Java script that "searches" for the AES decryption pattern automatically across any APK

# 💡 Main ideas

### **Path 1: The "Low-Level" Hacker (ProGuardCORE)**

**Repositories:** `proguard-core` \+ `proguard-assembler` This is the most impressive "Senior" move. Since you already solved UnCrackable L1, you can show how a security professional analyzes it at the **Bytecode level**.

* **The Video Idea:** *"Why JADX isn't enough: Manipulating Java Bytecode with ProGuardCORE."*  
* **The Flow:**   
  * 1\. Show the UnCrackable L1 code in JADX (what you've already done).   
  * 2\. Use **ProGuard Assembler** to "Disassemble" the class file into `.jbc` (readable assembly).   
  * 3\. Use **ProGuardCORE** to write a small Java script that "searches" for the AES decryption pattern automatically across any APK.  
* **Why it wins:** You are showcasing their **core library**. This proves you understand the engine behind ProGuard and DexGuard.

---

### **Path 2: The "Malware Researcher" (StrandHogg & Overlay)**

**Repositories:** `strandhogg-detection` or `android-overlay-detection` These are "Research" projects. They aren't tools for devs to use every day; they are **defensive techniques** against real-world Android malware (like banking trojans).

* **The Video Idea:** *"Defeating the Bank-Robber Malware: Detecting Android Overlay Attacks."*  
* **The Flow:**  
  1. Explain what an **Overlay Attack** is (a fake login screen on top of a real banking app).  
  2. Show the **insecure** code (how easy it is for an app to be covered).  
  3. Implement the code from the `android-overlay-detection` repo to "see" the attack happening.  
* **Why it wins:** It shows you stay up-to-date with **Guardsquare’s Security Research Center**. This is exactly the kind of "Thought Leadership" a DevRel person needs.

---

### **Path 3: The "DevSecOps" Specialist (GitHub Actions)**

**Repositories:** `appsweep-action` \+ `pivaa` Even if the AppSweep Web UI is paid, the **GitHub Action** often works for open-source repositories or has a "Community" usage tier for public repos.

* **The Video Idea:** *"Automating Security: Finding Vulnerabilities in Every Pull Request."*  
* **The Flow:**  
  1. Take the `pivaa` (Purposefully Insecure) app from their repo.  
  2. Set up a GitHub Repo and add the `appsweep-action`.  
  3. Show how the "Security Scan" automatically fails the build or highlights the `pivaa` vulnerabilities.  
* **Why it wins:** It’s practical. It tells the viewer: *"I'll show you how to get enterprise-grade security for your open-source project for free."*

# ❌ Discarded ideas:

**Hacking a vanilla APK vs Proguard APK:** 

* Find an open source android app. compile it to APK and then do reverse code hacking with jadx Do the same but now use proguard and explain differences in a video.  
* **❌ Problem:** Nobody uses proguard anymore, now Android provides a built-in tool called **R8** that does the same

**Dexguard tutorial**

* Since R8 became a thing now Guardsquare main product is Dexguard. Investigate and make a video about it  
* **❌ Problem:** It’s paid 💵 and expensive. So I can’t use it

**Using AppSweep with a hackable app:**

* Hack OWASP MASTG [Android UnCrackable L](https://mas.owasp.org/crackmes/Android#android-uncrackable-l1) and solve the problem. Then use AppSweep with the apk and check if it finds the issue. Make a video about that  
* **❌ Problem:**It’s paid 💵and expensive. So I can’t use it. Mailed them to ask for a trial version but they said no.

# 📽️ Relevant videos

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
* Jadx [https://www.youtube.com/watch?v=QlpDMmfOUmM](https://www.youtube.com/watch?v=QlpDMmfOUmM) 


# 🎓 What I learned

* **Intermediate Representation**, or **IR**:   
  modern compilers like **Clang/LLVM** actually use a "temporary bytecode" internally during the building process.  
  * **Front-End (Clang):** Turns your C++ into **LLVM Bitcode** (Intermediate Representation).\\  
  * **Optimizer:** Polishes that Bitcode to make it fast.  
  * **Back-End:** Finally turns that Bitcode into the **Machine Code** (.exe or ELF).  
  * **Crucially:** Usually, the developer "throws away" the Bitcode and only ships the Machine Code. Tools like **LibEBC** are for cases where the developer *chose* to embed that "recipe" inside the binary (common in Apple's ecosystem).

* **Embedded Bitcode (EBC)**:   
  When developers compile code for iOS (or sometimes Android native libraries), they don't always just produce the final machine code (the binary the phone runs). They can also embed LLVM Bitcode.  
  * Bitcode is an intermediate representation (**IR**)—it's halfway between source code and machine code.  
  * The Apple Use Case: Apple asks developers to upload Bitcode to the App Store so that Apple can re-compile and optimize the app for new iPhones without the developer needing to upload a new version.


* **Scoop:** handle different java versions in Windows  
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


