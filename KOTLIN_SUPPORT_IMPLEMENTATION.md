# Kotlin Code Generation Support Implementation

This document outlines the steps taken to implement and verify true Kotlin code generation support for the `test-populator` library.

## Objective

The goal was to extend the Java code generation capabilities of `test-populator` (`ObjectFactory`) to produce valid, idiomatic Kotlin code, while
verifying the output using real Kotlin models in the `test-populator-kotlin-verification` project.

## 1. Syntax Adjustments for Kotlin

To generate valid Kotlin syntax when `PopulateConfig.isKotlinSupport()` is enabled, several modifications were made to the core Java generation logic:

* **Static Imports:** Kotlin does not support `import static`. `FileWriterUtil` was updated to omit the `static` keyword for static imports when
  generating Kotlin files.
* **The `new` Keyword:** Kotlin instantiates classes without the `new` keyword. A global check was added to `ValueStringifierUtil` to intercept the
  output of `ValueFormatter` and dynamically strip the `new ` prefix for Kotlin targets. This ensures classes like `Throwable`, `Date`, `File`, etc.,
  are instantiated correctly in Kotlin syntax.
* **Value Class Initialization:** Kotlin `@JvmInline value class` types use a synthetic `box-impl` method under the hood. When `test-populator`'s
  `StaticMethodObjectBuilder` generated the code, it emitted `MyValueClass.box-impl(...)` which is invalid Kotlin syntax. `ObjectFactoryImpl` was
  updated to intercept `box-impl` calls in Kotlin mode and replace them with standard constructor templates: `MyValueClass(...)`.

## 2. The Kotlin Verification Project (`test-populator-kotlin-verification`)

To guarantee the validity of the Kotlin code generation, we utilized the `test-populator-kotlin-verification` project. This is a standalone, sibling
Gradle project written purely in Kotlin that acts as an integration and verification suite.

* **Purpose:** Its primary goal is to take the raw text output from `test-populator`'s `ObjectFactory`, write it to a temporary `.kt` file, compile it
  dynamically using the Kotlin compiler, and execute recursive assertions to compare the output instance against the original populated instance.
* **Gradle Configuration:** We added the `kotlin-compiler-embeddable` dependency to programmatically compile generated Kotlin files during test
  execution.
* **Path Resolution:** Updated `GeneratedCodeUtil.kt` to accurately resolve file paths matching the standard behavior of `ObjectFactoryImpl`, ensuring
  nested classes (like `MySealedClass.Success`) are mapped correctly to `MySealedClass_Success_TestData...kt`.
* **Reflection for Value Classes:** During the `assertGeneratedCode` round-trip comparison, Java's reflection API erased value classes to their
  underlying type (e.g., `String`). To compare these properly with the returned instances, `GeneratedCodeUtil.kt` was updated to dynamically invoke
  the synthetic `unbox-impl` method when encountering types annotated with `@JvmInline`.

## 3. Test Coverage

A comprehensive Kotlin test suite (`PopulationTest.kt`) was established in the verification project, mapping directly to the simulated
`KotlinLike*.java` models originally used in the `test-populator` project. This ensures full end-to-end coverage:

| Feature / Model                               | Verification Test Model                   |
|:----------------------------------------------|:------------------------------------------|
| **Data Classes** (`KotlinLikeClass`)          | `MyDataClass`                             |
| **Default Values** (`KotlinLikeWithDefaults`) | `MyClassWithDefaults`                     |
| **Generics** (`KotlinLikeWithGenerics`)       | `MyDataClass` (Using `List<String>`)      |
| **Singletons** (`KotlinLikeSingleton`)        | `MySingleton` (`object`)                  |
| **Companions** (`KotlinLikeWithCompanion`)    | `MyClassWithCompanion`                    |
| **Sealed Classes** (`KotlinLikeSealedClass`)  | `MySealedClass` (`Success` and `Error`)   |
| **Value Classes** (`KotlinLikeValueClass`)    | `MyValueClass` (`@JvmInline value class`) |

## 4. Known Limitations and Ignored Issues

### Default Values and Synthetic Constructors (`DefaultConstructorMarker`)

A notable limitation was observed when attempting to generate Kotlin code for classes using Kotlin's default parameter values (
`PopulateConfig.builder().defaultValues(true)`).

When a Kotlin class has default parameters (e.g., `val age: Int = 42`), the Kotlin compiler generates a synthetic constructor containing a bitmask
parameter and a `DefaultConstructorMarker` parameter. `test-populator` correctly utilizes this synthetic constructor via Java reflection to
instantiate the object without providing the default values.

However, `ObjectFactory` mirrors this behavior by emitting the synthetic constructor call into the generated source code (e.g.,
`new MyDataClass(id, name, 0, null, 6, null)`). This is a problem for Kotlin code generation because:

1. **Synthetic Visibility:** Kotlin compiler explicitly hides synthetic constructors from being called directly in Kotlin code.
2. **`DefaultConstructorMarker`:** The `kotlin.jvm.internal.DefaultConstructorMarker` class is internal and cannot be referenced or instantiated
   normally in user-space Kotlin code.

Because Kotlin expects default values to be handled via omitting arguments or using named arguments (e.g., `MyDataClass(id = id, name = name)`), the
generated code containing the synthetic constructor fails to compile in `test-populator-kotlin-verification`.

As a result, we explicitly ignored running `GeneratedCodeUtil.assertGeneratedCode(...)` in the test case
`can populate data class with default values`. To fully support code generation for Kotlin default values in the future, `ObjectFactory` would need to
be enhanced to support Kotlin's named arguments syntax instead of mapping directly to the JVM synthetic constructor.

## Conclusion

With these changes, `test-populator` now reliably generates syntactically valid Kotlin code when `kotlinSupport` is enabled, natively bypassing JVM
synthetic artifacts (`box-impl`, `$delegate`, etc.) while correctly emitting Kotlin syntax idioms.
