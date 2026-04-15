# Java Technical Concepts — Interview Cheat Sheet

> Context: Solutions Engineer at Guardsquare. The interview tests Java OOP fluency and the ability to understand *why* ProGuard/DexGuard break client code on first integration.

---

## 1. Java Compilation & Runtime Pipeline

```
Source code (.java)
    ↓  javac               ← part of the JDK
Bytecode (.class)          ← platform-independent instructions for the JVM
    ↓  jar                 ← part of the JDK
JAR (.jar)                 ← zip archive of .class files + META-INF/MANIFEST.MF
    ↓  (Android only) d8 / dx
DEX bytecode (.dex)        ← Dalvik/ART format for Android
    ↓  packaged into
APK (.apk)                 ← zip archive: classes.dex + resources + AndroidManifest.xml
```

### JDK / JRE / JVM

| Component | What it is | Role in the pipeline |
|---|---|---|
| **JDK** (Java Development Kit) | Full dev toolkit | Contains `javac` (compiler), `jar` tool, debugger, etc. Used at **build time** |
| **JRE** (Java Runtime Environment) | JVM + standard libraries | Used at **run time** on desktop/server. Android ships its own runtime (ART) instead |
| **JVM** (Java Virtual Machine) | The engine that executes bytecode | Interprets/JIT-compiles `.class` files at **run time**. Android uses **ART** (ahead-of-time) instead of a classic JVM |

### Where .jar fits

A `.jar` is a **zip of `.class` files**. It sits between compilation and packaging:

- On **standard Java**: the JVM runs `.jar` files directly.
- On **Android**: `d8`/`dx` reads the `.class` files inside the `.jar` and compiles them down to `.dex` — the `.jar` is a **build-time intermediate**, never shipped to the device.

This is why tools like `dex2jar` reverse the process: they reconstruct a `.jar` from a `.dex` so you can inspect it with Java tooling (like JADX or `javap`).

**ProGuard/DexGuard operates on the `.class` / `.dex` level** — after compilation, before or during packaging. It shrinks (removes unused code), optimizes (rewrites bytecode), and obfuscates (renames identifiers).

### Key bytecode instructions (from the challenge)
| Instruction | Meaning |
|---|---|
| `invokevirtual` | call instance method (resolved at runtime via vtable) |
| `invokestatic` | call static method |
| `invokespecial` | call constructor / super |
| `invokeinterface` | call interface method |
| `ifeq` / `ifne` | conditional branch (0 == / 0 !=) |
| `checkcast` | runtime type check; throws `ClassCastException` |
| `aload_N` | load reference from local variable slot N |
| `astore_N` | store reference into local variable slot N |

---

## 2. JVM Fundamentals

### Memory Areas
| Area | Per | Purpose |
|---|---|---|
| **Heap** | JVM | Objects live here; garbage collected |
| **Stack** | Thread | Method call frames (local vars, operand stack) |
| **Method Area / Metaspace** | JVM | Class metadata, static fields, bytecode |
| **PC Register** | Thread | Current instruction pointer |
| **Native Method Stack** | Thread | JNI native frames |

### Class Loading — Three-Phase Lifecycle
1. **Loading** — find the `.class` bytes and create a `Class` object  
2. **Linking**  
   - *Verify* — check bytecode is valid and safe  
   - *Prepare* — allocate static fields with default values  
   - *Resolve* — replace symbolic references with direct references  
3. **Initialization** — run static initializers (`static {}` blocks)

### Class Loader Hierarchy (Parent Delegation)
```
Bootstrap ClassLoader  (loads rt.jar / core libs)
    └── Extension ClassLoader
            └── Application ClassLoader  (loads classpath)
                    └── (custom ClassLoaders — e.g., Android's PathClassLoader)
```
Parent is always asked first. A class is identified by its **fully qualified name + ClassLoader**. Two classes with the same name loaded by different loaders are different types.

---

## 3. Core OOP

### Classes & Objects
- A **class** is a blueprint (type); an **object** is an instance (value on the heap).
- `new` keyword: allocates heap memory + calls constructor.
- `this` refers to the current instance; `super` refers to the parent type.

### Inheritance (`extends`)
```java
class Animal { void speak() { ... } }
class Dog extends Animal { @Override void speak() { ... } }
```
- Java supports **single inheritance** of classes (but multiple interface implementation).
- `Object` is the implicit root of every class hierarchy.
- Constructor chaining: `super(...)` must be the first statement if called explicitly; the compiler inserts a no-arg `super()` implicitly otherwise.

### Polymorphism
**Runtime (dynamic dispatch)** — the actual method called depends on the object's runtime type, not the reference type. This is resolved via the **vtable**.
```java
Animal a = new Dog();
a.speak();  // calls Dog#speak(), not Animal#speak()
```
**Compile-time (overloading)** — same method name, different parameter types. Resolved at compile time.

> **Obfuscation impact**: method renaming can collapse overloaded methods into the same short name `a()`, `b()`, etc. Runtime dispatch still works because the vtable is rebuilt. Problems arise only when code references names as strings.

### Encapsulation
Hiding internal state. Achieved with **access modifiers** + getters/setters.

### Abstraction
Expressing *what* without *how*. Achieved with **abstract classes** and **interfaces**.

---

## 4. Interfaces vs Abstract Classes

| | Abstract Class | Interface |
|---|---|---|
| `extends` / `implements` | `extends` (single) | `implements` (multiple) |
| Constructor | Yes | No |
| Instance fields | Yes | No (only `public static final`) |
| Method bodies | Yes (abstract + concrete) | Default methods (Java 8+) |
| Access modifiers | Any | `public` by default |
| State | Can hold state | Stateless by nature |

**Rule of thumb:**
- Use **interface** when you define a contract/capability (e.g., `Serializable`, `Comparable`).
- Use **abstract class** when you share code + state across related types.

```java
// Interface with default method (Java 8+)
interface Greeter {
    String greet(String name);                          // abstract
    default String greetLoudly(String name) {          // concrete
        return greet(name).toUpperCase();
    }
}

// Abstract class
abstract class Shape {
    protected String color;                             // shared state
    abstract double area();                             // must override
    void describe() { System.out.println(color); }     // shared behavior
}
```

---

## 5. Access Modifiers

| Modifier | Class | Package | Subclass | World |
|---|:---:|:---:|:---:|:---:|
| `public` | ✓ | ✓ | ✓ | ✓ |
| `protected` | ✓ | ✓ | ✓ | ✗ |
| *(none / package-private)* | ✓ | ✓ | ✗ | ✗ |
| `private` | ✓ | ✗ | ✗ | ✗ |

> **Obfuscation impact**: ProGuard can change `public` / `protected` modifiers while renaming to maximize name reuse. Public API classes that are part of the keep configuration must retain their original modifiers.

---

## 6. Static vs Instance

| | Instance | Static |
|---|---|---|
| Belongs to | Object | Class |
| Access via | reference | class name (or reference, discouraged) |
| Memory | on the heap per object | in Method Area, shared |
| `this` available | Yes | No |

**Static initializer block** — runs once when the class is loaded:
```java
class Config {
    static final String KEY;
    static {
        KEY = System.getenv("APP_KEY");  // runs at class initialization
    }
}
```

---

## 7. Inner Classes

| Type | Declaration | Access to outer | Typical use |
|---|---|---|---|
| **Static nested** | `static class Foo` inside class | No | Logically grouped helper |
| **Inner (non-static)** | `class Foo` inside class | Yes (holds ref to outer) | Iterator, Builder |
| **Local** | Inside a method | Yes (effectively final vars) | Rare |
| **Anonymous** | `new Interface() { ... }` | Yes (effectively final vars) | Listeners, callbacks |

```java
// Anonymous class — common in Android pre-lambda
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) { ... }
});
```

> **Obfuscation note**: anonymous classes become `ClassName$1`, `ClassName$2` in bytecode. ProGuard renames these to short names. You saw MainActivity$1 and MainActivity$2 in the challenge.

---

## 8. Generics & Type Erasure

Generics exist only at **compile time**. The compiler inserts casts and removes type parameters — this is **type erasure**.
```java
List<String> list = new ArrayList<>();
// At bytecode level this is just: List list = new ArrayList();
// Casts to String are inserted wherever elements are retrieved.
```

**Consequence**: you cannot do `new T()`, `instanceof T`, or `T.class` at runtime — T is gone.

**Wildcards**:
```java
List<? extends Number>   // read-only: producer / covariant
List<? super Integer>    // write-only: consumer / contravariant
```

> **Obfuscation impact**: because generics are erased, obfuscation doesn't interact with type parameters directly. But `instanceof` checks on parameterized types are illegal anyway. The risk is with reflection that reads generic signatures from attributes which ProGuard may strip.

---

## 9. Reflection API

Reflection lets code inspect and invoke types, methods, and fields **by name at runtime**.

```java
// Common patterns that BREAK after obfuscation
Class<?> clazz = Class.forName("com.example.MyService");  // name hardcoded!
Method method = clazz.getMethod("processData", String.class);
method.invoke(instance, "payload");

// Field access
Field field = clazz.getDeclaredField("secretKey");
field.setAccessible(true);
Object value = field.get(instance);
```

**Why this breaks**: after obfuscation `com.example.MyService` is renamed to `a.b.c`, so `Class.forName("com.example.MyService")` throws `ClassNotFoundException`.

**Fix — ProGuard keep rule**:
```
-keep class com.example.MyService { *; }
-keepclassmembers class com.example.MyService {
    public void processData(java.lang.String);
}
```

**Common reflection entry points to watch for**:
- `Class.forName(String)` — hardcoded class name
- `Method.invoke()` / `Field.get()` — usually safe if the class is kept
- Dependency injection frameworks (Dagger, Hilt, Spring) — use annotation processors
- JSON mapping (Gson, Jackson) — map field names by reflection; field names must be kept
- `getClass().getName()` / `.getSimpleName()` — returns obfuscated name

---

## 10. Serialization

A class that implements `java.io.Serializable` can be written to bytes and restored later. The mechanism:
1. JVM writes the class name + all non-`transient` fields by name to the stream.
2. Deserialization reads the bytes, finds the class by name, and sets fields by name.

```java
class User implements Serializable {
    private static final long serialVersionUID = 1L;  // version stamp
    private String username;
    private transient String password;  // NOT serialized
}
```

**Why this breaks after obfuscation**:
- Class `com.example.User` → renamed to `a.b.a` → deserialization fails: class not found
- Field `username` → renamed to `a` → field not found in the stream
- `serialVersionUID` not declared → JVM auto-computes it from class structure → changes every build after obfuscation → `InvalidClassException`

**Fix**:
```
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
```

---

## 11. Annotations

```java
@Retention(RetentionPolicy.RUNTIME)   // visible at runtime via reflection
@Retention(RetentionPolicy.CLASS)     // in .class file, stripped by JVM at load time
@Retention(RetentionPolicy.SOURCE)    // only in source, stripped by compiler
```

**Why annotations break after obfuscation**:
- `CLASS` and `SOURCE` annotations are usually fine (no runtime use).
- `RUNTIME` annotations read via reflection may break if the annotated element is renamed.
- Frameworks that use annotations (Spring `@Component`, Android `@Inject`) crawl class names at runtime — those classes must be kept.

---

## 12. Enums

```java
enum Status { ACTIVE, INACTIVE }
```

Under the hood, enums are classes extending `java.lang.Enum`. They have:
- `name()` — returns the declared name as a string (e.g. `"ACTIVE"`)
- `valueOf(String)` — looks up by declared name
- `ordinal()` — position index

**Why this breaks after obfuscation**:
- If ProGuard renames enum constants (which it can do if not kept), `Status.valueOf("ACTIVE")` throws `IllegalArgumentException`.
- Serialization of enums uses `name()`, so same problem as above.

**Fix**:
```
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
```

---

## 13. Native Methods (JNI)

```java
public class CryptoLib {
    static { System.loadLibrary("crypto-native"); }   // loads libcrypto-native.so
    public native byte[] encrypt(byte[] data, byte[] key);
}
```

The native library looks up the Java method by its **JNI mangled name**: `Java_com_example_CryptoLib_encrypt`. If ProGuard renames `CryptoLib` or `encrypt`, the native lookup will silently fail at runtime (`UnsatisfiedLinkError`).

**Fix**:
```
-keepclasseswithmembernames class * {
    native <methods>;
}
```

---

## 14. What Breaks & Why — Quick Reference

| Scenario | Root cause | Fix |
|---|---|---|
| `Class.forName("com.example.Foo")` → `ClassNotFoundException` | Class renamed by obfuscation | `-keep class com.example.Foo` |
| JSON (de)serialization field mismatch | Field names renamed | `-keepclassmembers` or `@SerializedName` |
| `InvalidClassException` on deserialization | `serialVersionUID` changed | Declare explicit `serialVersionUID`; keep Serializable members |
| `valueOf("ACTIVE")` on enum → exception | Enum constant renamed | `-keepclassmembers enum *` |
| `UnsatisfiedLinkError` on native call | Method/class renamed, JNI name broken | `-keepclasseswithmembernames` on native methods |
| Dependency injection fails | DI framework uses reflection on class names | Keep injected classes/constructors |
| Annotation-driven framework stops working | Annotated class renamed | Keep annotated classes |
| `ClassCastException` after optimization | Aggressive class merging / inlining | Reduce optimization passes or add `-dontoptimize` |
| Stack trace unreadable | Method names are `a()`, `b()` | Use retrace with the mapping.txt ProGuard generates |

---

## 15. Android Specifics

### APK Structure (zip archive)
```
classes.dex          ← compiled Java/Kotlin code (Dalvik bytecode)
AndroidManifest.xml  ← binary XML: package name, permissions, components
res/                 ← compiled resources
resources.arsc       ← resource table (maps R.id → actual resource)
META-INF/            ← signing certificates (CERT.RSA, CERT.SF, MANIFEST.MF)
lib/                 ← native .so libraries per ABI
assets/              ← raw files bundled as-is
```

### DEX vs JVM Bytecode
| | JVM `.class` | Android `.dex` |
|---|---|---|
| Format | One file per class | All classes in one file |
| Registers | Stack-based VM | Register-based VM |
| max methods | Unlimited per class | **65,536 per dex** (multidex for larger apps) |
| Tools | `javap`, `ASM` | `dexdump`, `baksmali`, ProGuard Assembler |

### ART (Android Runtime)
- Replaced Dalvik since Android 5.0.
- Compiles DEX to native machine code at **install time** (AOT) or **first run** (JIT + profile-guided).
- ProGuard/DexGuard run **before** ART sees the code.

### Android Class Loading
- `PathClassLoader` — loads installed APK classes.
- `DexClassLoader` — loads DEX from any path (used for plugins, hot-patching).
- Both extend `BaseDexClassLoader`.

---

## 16. Common OOP Interview Patterns

### SOLID Principles (quick recall)
| Principle | One-line |
|---|---|
| **S**ingle Responsibility | A class should have one reason to change |
| **O**pen/Closed | Open for extension, closed for modification |
| **L**iskov Substitution | Subtypes must be substitutable for their base type |
| **I**nterface Segregation | Many small interfaces > one large interface |
| **D**ependency Inversion | Depend on abstractions, not concretions |

### `equals()` and `hashCode()` contract
- If `a.equals(b)` then `a.hashCode() == b.hashCode()` must hold.
- Override both or neither. Used by `HashMap`, `HashSet`.

### `final` keyword
| Context | Meaning |
|---|---|
| `final class` | Cannot be subclassed (e.g., `String`) |
| `final method` | Cannot be overridden |
| `final variable` | Cannot be reassigned (reference; the object itself can mutate) |

> ProGuard uses `final` information for optimization — it can inline `final` methods.

### Exception Hierarchy
```
Throwable
├── Error          (JVM errors — don't catch: OutOfMemoryError)
└── Exception
    ├── RuntimeException    (unchecked — don't need to declare: NullPointerException)
    └── (checked)           (must catch or declare: IOException, ClassNotFoundException)
```

---

## 17. Guardsquare Tools — What They Do

| Tool | Target | What it does |
|---|---|---|
| **ProGuard** | JVM / Android | Open-source: shrink + optimize + obfuscate `.class` / `.dex` |
| **DexGuard** | Android | Commercial, ProGuard superset: advanced obfuscation, runtime protection, tamper detection |
| **iXGuard** | iOS | Obfuscation and hardening for Swift/Obj-C frameworks |

### ProGuard Pipeline
```
Input JARs/AARs
    ↓  Shrink (remove unused classes/methods/fields via usage trace from entry points)
    ↓  Optimize (bytecode rewrites: constant folding, method inlining, etc.)
    ↓  Obfuscate (rename classes → a, b, c...; methods → a, b, c...)
    ↓  Preverify (add StackMapTable attributes)
Output JARs + mapping.txt
```

### Entry Points / Keep Configuration
ProGuard must be told what the "roots" of the usage tree are (what code is accessed from outside the JAR). Common entry points:
```
-keep public class * extends android.app.Activity     # Activities
-keep public class * extends android.app.Service      # Services
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;    # JS bridge
}
```

### `mapping.txt`
Generated every build. Maps obfuscated names back to originals. Required for:
- Reading crash stack traces (use `retrace`)
- Reproducing bugs reported by users

---

## 18. Quick Java Syntax Recall

```java
// Generics with bounded type
public <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}

// Varargs
void log(String format, Object... args) { ... }

// Try-with-resources (auto-closes Closeable)
try (InputStream in = new FileInputStream(file)) { ... }

// Lambda (Java 8+) — implements functional interface
Runnable r = () -> System.out.println("hello");
List<String> sorted = list.stream()
    .filter(s -> s.startsWith("A"))
    .sorted()
    .collect(Collectors.toList());

// Optional
Optional<String> opt = Optional.ofNullable(getValue());
String result = opt.orElse("default");
```

---

## 19. Things That Are Fine After Obfuscation

Not everything breaks. Good to be able to say what is safe:
- Pure logic: arithmetic, control flow, instance creation — **fully safe**
- Method calls via direct references (not strings): `obj.process()` — **safe** (vtable updated)
- Inheritance and polymorphism: vtables are rebuilt — **safe**
- Interfaces implemented internally: **safe**
- Anonymous classes / lambdas used internally: **safe**
- Logging strings: lose class/method names in output, but don't crash — **safe**
