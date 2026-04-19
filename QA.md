# Q&A

## Java development
**Q: How to install Java?**
To start a Java project, you can follow these steps:
Install Java with scoop:
```
scoop install openjdk8-redhat
scoop reset openjdk8-redhat
java -version
javac -version
```

Version should be `1.8.0_342` or similar.

**Q: Compile and run Java**
```sh
javac -d bin ./src/main/java/com/yourcompany/Main.java
java -cp bin com.yourcompany.Main
Hello world!
```

**Q: When to use == vs .equals() in Java?**
In Java, `==` is used to compare primitive types (like `int`, `char`, etc.) and to check if two object references point to the same object in memory. On the other hand, `.equals()` is a method that is used to compare the contents of two objects for equality (like `String`, `Integer`, etc.). It is important to use `.equals()` when comparing objects for logical equality, as it checks the actual data within the objects rather than just their memory addresses.

**Q: What is static vs instance methods in Java and how does it affect ProGuard/DexGuard?**
Static methods belong to the class and can be called without creating an instance of the class, while instance methods belong to an instance of the class and require an object to be called. ProGuard/DexGuard will obfuscate both static and instance methods, but it is important to use -keep rules to prevent obfuscation of critical static methods that may be accessed via reflection or used as entry points in the application. For instance methods, you should also use -keep rules if they are accessed via reflection or if they are part of the public API that needs to be preserved for external libraries or frameworks. Properly configuring ProGuard/DexGuard with -keep rules can help ensure that critical static and instance methods are not obfuscated, while still allowing for effective code obfuscation for security purposes.

**Q: How to parse JSON in Java without using external libraries nor reflection?**
Using `org.json` library (`JSONObject.getString()`): this is an external dependency but it is not reflection-based  — it parses the JSON string directly into a map-like structure, so field names are never involved:
```java
import org.json.JSONObject;

JSONObject obj = new JSONObject("{\"name\": \"Pablo\", \"balance\": \"1000\"}");
String name    = obj.getString("name");    // "Pablo"
String balance = obj.getString("balance"); // "1000"
```

Because `org.json` does not use reflection on your classes (it just parses raw text), **ProGuard/DexGuard obfuscation has zero impact** on this approach — there are no field names to rename. This is the key difference vs. Gson/Jackson.

### Gradle

**Q: Make a Gradle project**
Gradle it's a project manager that helps you manage dependencies, compile and run the project. 
If you run `gradle init` in an empty directory, it will create a basic Gradle project structure with these files and directories:
- `src/main/java`: where your source code goes
- `src/test/java`: where your test code goes
- `gradle/wrapper`: contains the Gradle Wrapper files (`gradle-wrapper.jar` and `gradle-wrapper.properties`) which allow you to run Gradle without having it installed globally on your system.
- `build.gradle`: the build script. Defines the build configuration, dependencies, and tasks.
- `settings.gradle`: the settings script. Defines the project name and module structure (for multi-module projects).
- `.gradle`: an internal directory used by Gradle to store cache and other build-related files (auto-generated, not manually edited)
- `gradlew` and `gradlew.bat`: scripts to execute Gradle tasks on Linux/Mac and Windows respectively, using the Gradle Wrapper.
- `gradle.properties`: optional, can be used to define project properties and settings such as
- `gradle/libs.versions.toml`: optional, can be used to define dependency versions in a centralized way.

```
gradle init --type java-application --dsl groovy --package com.pablorotten --project-name ProGuardLab
```

Generates this structure:
```
│   .gitattributes
│   .gitignore
│   gradle.properties
│   gradlew
│   gradlew.bat
│   settings.gradle
│   
├───app
│   │   build.gradle
│   │   
│   └───src
│       ├───main
│       │   ├───java
│       │   │   └───com
│       │   │       └───pablorotten
│       │   │               App.java
│       │   │               
│       │   └───resources
│       └───test
│           ├───java
│           │   └───com
│           │       └───pablorotten
│           │               AppTest.java
│           │               
│           └───resources
└───gradle
    │   libs.versions.toml
    │   
    └───wrapper
            gradle-wrapper.jar
            gradle-wrapper.properties
```     

**Q: How to work in a Gradle project?**

#### Define project JDK version:

Easiest way is to use the foojay.io plugin. It will automatically download the specified JDK version and use it for compilation.

Add the plugin to `settings.gradle`:
```groovy
plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
}
```

Then on `app/build.gradle` you can specify the JDK version like this:
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

#### Compile the project:

To build and run the project, you can use:
```
./gradlew :app:run
```
Clean and build the project:
```
./gradlew :app:clean :app:build
``` 

#### Add dependencies:

Modern way (recommended) 

On `gradle/libs.versions.tom` you can add dependencies like this:
```toml
[versions]
gson = "2.8.9"
[libraries]
gson = { module = "com.google.code.gson:gson", version.ref = "gson" }
```

Then on `app/build.gradle` you can add the dependency like this:
```groovy
dependencies {
    implementation libs.gson
}
```
Old way (not recommended)

Or you can add it directly on `app/build.gradle` like this:
```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

## JVM
**Q: Is there a mapping or a metadata created when java classes compiles? How does it interact with ProGuard/DexGuard?**
**Q: What is link/loading time and how does it affect ProGuard/DexGuard?**
**Q: How does the class loader work in Java and how does it affect ProGuard/DexGuard?**
**Q: Where exactly are the class loaded? In RAM memory? But how, how do they look like? Are they translated to machine code? How does it affect ProGuard/DexGuard?**
**Q: Can an app use a custom ClassLoader to load classes at runtime? How does it look like?If so, how does it affect ProGuard/DexGuard?**
**Q: How does annotations or configuration files work in Java and how does it affect ProGuard/DexGuard?**
**Q: Does JVM verify the bytecode before executing it? If so, how does it affect ProGuard/DexGuard?**
**Q: How does JIT (Just-In-Time) compilation work in Java and how does it affect ProGuard/DexGuard?**
**Q: How does JVM treat static vs instance methods?** 

## OOP
**Q: Does public/private methods matter to ProGuard/DexGuard?**
No, ProGuard and DexGuard will process all methods, regardless of their access modifiers. However, it is generally recommended to keep public methods that are part of the API or used for reflection, as obfuscating them may cause issues with external libraries or frameworks that rely on them.

For example:
```java
public class MyClass {
    public void publicMethod() {
        // This method will be processed by ProGuard/DexGuard
    }

    private void privateMethod() {
        // This method will also be processed by ProGuard/DexGuard
    }
}


```
**Q: How does private and public affect the way ProGuard/DexGuard obfuscates code?**
**Q: How Polymorphism works with ProGuard/DexGuard? Differences when inheritance is via interfaces or abstract classes and common pitfalls**

## Reflection
**Q: What are the common issues with reflection?**
**Q: Why use reflection instead of direct reference to classes/methods/fields? In what situations is it useful?**
**Q: How to use reflection without hardcoding class/method/field names?**
**Q: Using the -keep flags it's the goto solution for reflection  how to keep balance between security and functionality?**
**Q: Is this a good practice `Class<?> myClass = ClassLoader.getSystemClassLoader().loadClass("com.pablo.MySecretService");` or `Class.forName("Name")`? In what situations is it useful? Are there safer alternatives from ProGuard/DexGuard POV?**
Using `ClassLoader.getSystemClassLoader().loadClass("com.pablo.MySecretService")` is generally not recommended, as it can lead to security vulnerabilities and maintenance issues. It relies on hardcoded class names, which can break if the class is renamed or obfuscated by ProGuard/DexGuard. A safer alternative would be to use a more dynamic approach, such as using annotations or configuration files to specify the classes that need to be accessed via reflection, and then using ProGuard/DexGuard's -keep rules to ensure those classes are not obfuscated. For example, you could define an annotation like `@KeepForReflection` and annotate the classes that need to be accessed via reflection, and then use a ProGuard/DexGuard rule like `-keep @interface com.pablo.annotations.KeepForReflection` to keep those classes from being obfuscated. This way, you can maintain a balance between security and functionality while still allowing for dynamic class loading without hardcoding class names.

## Android
**Q: In Android apps, how to deal with Activity, MainActivity, and other components when using ProGuard/DexGuard? Is always using -keep rules necessary?**
**Q: What is AndroidManifest.xml and how does it interact with ProGuard/DexGuard?**
**Q: How to handle ProGuard/DexGuard with Android libraries and dependencies?**

## External Libraries and frameworks
**Q: If a library is obfuscated with ProGuard/DexGuard, how does it affect the main application that uses it? Can it provide an non-obfuscated Public API?**    
**Q: What are typical examples of reflection with external libraries such as Gson, Spring, Hibernate, etc.? What commone issues have Proguard/Dexguard with those?**

Gson example — how reflection is used to deserialize JSON into a Java object**

Given this JSON:
```json
{"name": "Pablo", "balance": "1000"}
```

And this `User` class:
```java
public class User {
    private String name;
    private String balance;
}
```

This is how Gson deserializes the JSON — **using reflection internally**:
```java
// What you write
Gson gson = new Gson();
User user = gson.fromJson("{\"name\": \"Pablo\", \"balance\": \"1000\"}", User.class);
```


What Gson does internally:
```java
Class<?> clazz = User.class;                          // 1. Get the Class object

Object instance = clazz.getDeclaredConstructor()      // 2. Create an empty instance
                       .newInstance();                 //    via reflection (no args constructor)

for (Field field : clazz.getDeclaredFields()) {       // 3. Iterate over all declared fields
    field.setAccessible(true);                        //    bypass private access
    String jsonValue = json.get(field.getName());     //    look up field name in the JSON
    field.set(instance, jsonValue);                   //    inject the value
}
// field.getName() returns "name" and "balance" — the original field names
```

**After obfuscation**, ProGuard renames the class and its fields:
```java
public class A {        // User → A
    private String a;   // name → a
    private String b;   // balance → b
}
```

Now `field.getName()` returns `"a"` and `"b"` — which don't exist as keys in the JSON. Gson silently sets them to `null`:
```java
user.getName()    // → null  (expected "Pablo")
user.getBalance() // → null  (expected "1000")
```

**Fix 1 — `@SerializedName` annotation** (preferred): hardcodes the JSON key directly in the source, survives obfuscation because the annotation value is a string constant in bytecode:
```java
public class User {
    @SerializedName("name")
    private String name;

    @SerializedName("balance")
    private String balance;
}
```

**Fix 2 — ProGuard `-keep` rule**: prevents obfuscation of the class and its fields entirely:
```
-keep class com.yourpackage.User { <fields>; }
```

`@SerializedName` is generally preferred because it keeps the security benefit of obfuscating the class name (`User` → `A`) while still mapping field names correctly.

**Q: In this GSON example, can't proguard/dexguard detect that the fields are accessed via reflection and keep them? Does the developer need to manually add -keep rules for each class that he knows it's used for reflection?**
ProGuard/DexGuard cannot automatically detect all cases of reflection, especially when the class and field names are not explicitly referenced in the code. In the case of Gson, it uses reflection to access the fields of the User class based on their names as specified in the JSON. ProGuard/DexGuard does not have the context to understand that these fields are being accessed via reflection, so it cannot automatically keep them. Therefore, developers need to manually add -keep rules for each class and its fields that they know are accessed via reflection to ensure that ProGuard/DexGuard does not obfuscate them. For example, you would need to add a rule like `-keep class com.yourpackage.User { private String name; private String balance; }` to prevent ProGuard/DexGuard from obfuscating the User class and its fields, allowing Gson to properly deserialize the JSON data without issues.



## ProGuard/DexGuard 
**Q: How does Mapping file work in ProGuard/DexGuard?**
**Q: Different issues on compile-time, link/loading-time, and runtime when using ProGuard/DexGuard?**

 - java.lang.ClassNotFoundException: This can occur if ProGuard/DexGuard has obfuscated a class that is being accessed via reflection, and the class name has been changed. To resolve this, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the classes that are accessed via reflection.
 - java.lang.NoSuchMethodException: This can occur if ProGuard/DexGuard has obfuscated a method that is being accessed via reflection, and the method name has been changed. Similar to the previous issue, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the methods that are accessed via reflection.
 - java.lang.NoSuchFieldException: This can occur if ProGuard/DexGuard has obfuscated a field that is being accessed via reflection, and the field name has been changed. Again, using -keep rules to prevent ProGuard/DexGuard from obfuscating the fields that are accessed via reflection can help resolve this issue.
 - java.lang.ClassCastException: This can occur if ProGuard/DexGuard has obfuscated a class that is being cast to another type, and the class name has been changed. To resolve this, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the classes that are being cast.
 - java.lang.IllegalAccessException: This can occur if ProGuard/DexGuard has obfuscated a class, method, or field that is being accessed, and the access level has been changed. To resolve this, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the classes, methods, or fields that are being accessed.
 - java.lang.VerifyError: This can occur if ProGuard/DexGuard has obfuscated code in a way that violates the Java bytecode verification rules. To resolve this, you can review your ProGuard/DexGuard configuration to ensure that it is not obfuscating code in a way that violates these rules, and use -keep rules to prevent obfuscation of critical code sections.
 - java.lang.OutOfMemoryError: This can occur if ProGuard/DexGuard is configured to keep too many classes or methods, which can lead to an increase in the size of the resulting APK and cause memory issues during runtime. To resolve this, you can review your ProGuard/DexGuard configuration to ensure that you are only keeping the necessary classes and methods, and consider using more aggressive obfuscation settings to reduce the size of the resulting APK.
 - java.lang.StackOverflowError: This can occur if ProGuard/DexGuard is configured to keep too many classes or methods, which can lead to an increase in the size of the resulting APK and cause stack overflow issues during runtime. To resolve this, you can review your ProGuard/DexGuard configuration to ensure that you are only keeping the necessary classes and methods, and consider using more aggressive obfuscation settings to reduce the size of the resulting APK.
 - java.lang.NoClassDefFoundError: This can occur if ProGuard/DexGuard has obfuscated a class that is being accessed, and the class definition cannot be found at runtime. To resolve this, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the classes that are being accessed, and ensure that all necessary classes are included in the resulting APK.
 - java.lang.UnsupportedOperationException: This can occur if ProGuard/DexGuard has obfuscated code in a way that violates the expected behavior of the application, such as changing method signatures or access levels. To resolve this, you can review your ProGuard/DexGuard configuration to ensure that it is not obfuscating code in a way that violates the expected behavior of the application, and use -keep rules to prevent obfuscation of critical code sections.
 - java.lang.ClassFormatError: This can occur if ProGuard/DexGuard has obfuscated code in a way that results in invalid bytecode. To resolve this, you can review your ProGuard/DexGuard configuration to ensure that it is not obfuscating code in a way that results in invalid bytecode, and use -keep rules to prevent obfuscation of critical code sections.

**Q: Can ProGuard/DexGuard introduce errors that could have been detected at compile-time?**
Yes, ProGuard/DexGuard runs after the code has been compiled, and it can introduce errors that were not present during compile-time. For example, if ProGuard/DexGuard obfuscates a class or method that is accessed via reflection, it can lead to runtime errors such as `ClassNotFoundException`, `NoSuchMethodException`, or `NoSuchFieldException`. Additionally, if ProGuard/DexGuard obfuscates code in a way that violates Java bytecode verification rules, it can lead to `VerifyError` at runtime. Therefore, it is important to carefully configure ProGuard/DexGuard and use -keep rules to prevent obfuscation of critical code sections to avoid introducing errors that could have been detected at compile-time.

**Q: If in the code I extend a class from a external library that is not obfuscated, will it cause any issues with ProGuard/DexGuard like renaming an extended method?**
If you extend a class from an external library that is not obfuscated, ProGuard/DexGuard will still obfuscate the methods in your subclass, but it will not rename the methods in the external library. However, if you override a method from the external library in your subclass, ProGuard/DexGuard may obfuscate the method name in your subclass, which can lead to issues if the external library relies on reflection to access that method. To avoid this issue, you can use -keep rules to prevent ProGuard/DexGuard from obfuscating the overridden methods in your subclass that are accessed via reflection by the external library. For example, if you have a method `public void doSomething()` in the external library that is overridden in your subclass, you can use a ProGuard/DexGuard rule like `-keep class com.yourpackage.YourSubclass { public void doSomething(); }` to prevent ProGuard/DexGuard from obfuscating the `doSomething()` method in your subclass, ensuring that it can still be accessed via reflection by the external library without issues.



## Miscellaneous
**Q: GraalVM**