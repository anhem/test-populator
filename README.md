# Test-Populator

[![CI](https://github.com/anhem/test-populator/workflows/CI/badge.svg)](https://github.com/anhem/test-populator/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anhem_test-populator&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anhem_test-populator)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.anhem/test-populator.svg)](https://central.sonatype.com/artifact/com.github.anhem/test-populator/)
[![OpenJDK](https://img.shields.io/badge/OpenJDK-11-brightgreen)](https://github.com/anhem/test-populator/blob/main/pom.xml#L26)

Test-Populator is a Java library that automatically creates and populates objects with fixed or random data, simplifying the creation of test data.

-----

## Why Use Test-Populator?

When writing tests, you often need an instance of a class but don't care about all the specific data it contains. Manually creating and populating
these objects can be time-consuming, adds extra code to maintain, and can lead to complex mocking setups.

This library solves that problem by automatically creating and populating objects for you.

For example, instead of manually instantiating `MyClass` and all its nested objects, you can simply do this:

```java
MyClass myClass = new PopulateFactory().populate(MyClass.class);
```

Given the following class definition:

```java
public class MyClass {

    private final String stringValue;
    private final List<ArbitraryEnum> listWithEnums;
    private final InnerClass myInnerClass;

    // Constructor...

    public static class InnerClass {
        private final int integer;
        private final Map<String, LocalDate> stringToLocalDateMap;

        // Constructor...
    }
}
```

The library will generate a result like this:

```
// Console output from myClass.toString()
MyClass{
    stringValue='xksqbhddha', 
    listWithEnums=[B], 
    myInnerClass=InnerClass{
        integer=789707, 
        stringToLocalDateMap={dsyyjxizvp=2021-02-14}
    }
}
```

-----

## Key Features

* **Automatic Object Population**: Instantly create fully-populated, complex Java objects with a single line of code.
* **Multiple Creation Strategies**: Intelligently creates objects using a configurable chain of strategies (constructor, setters, builders, etc.) to
  handle almost any class design.
* **Highly Configurable**: Tailor the object creation logic to your exact needs. You can generate random or fixed (deterministic) data, provide custom
  logic for specific types (like `UUID`), handle circular dependencies, and more.
* **Builder Support**: Natively supports common builder patterns from libraries like **[Lombok](https://projectlombok.org/)**,
  **[Immutables](https://immutables.github.io/)**, and **[Protobuf](https://protobuf.dev/)**.
* **Java Code Generation (Experimental)**: Automatically generate the Java source code for the populated objects, which you can then save and reuse in
  your tests.

-----

## Configuration

You can customize the library's behavior using a `PopulateConfig` object. If you don't provide one, default settings will be used.

The basic setup flow is:

1. **Configure**: Create a `PopulateConfig` instance with your desired settings.
2. **Set up**: Pass the config to a new `PopulateFactory`.
3. **Use**: Call `populate()` on the factory instance.

<!-- end list -->

```java
// 1. Configure
PopulateConfig populateConfig = PopulateConfig.builder()
                .randomValues(false) // Use fixed values instead of random
                .nullOnCircularDependency(true) // Prevent stack overflows
                .build();

// 2. Set up
PopulateFactory populateFactory = new PopulateFactory(populateConfig);

// 3. Use
MyClass myClass = populateFactory.populate(MyClass.class);
```

### Main Configuration Options

#### `strategyOrder`

Defines the order of strategies to try when creating an object. If the first strategy fails, it moves to the next.

* **Default**: `CONSTRUCTOR`, `SETTER`, `STATIC_METHOD`
* **Available Strategies**:
    * `CONSTRUCTOR`: Uses the constructor with the most parameters.
    * `SETTER`: Uses a no-arg constructor, then calls standard setter methods (e.g., `setName()`).
    * `MUTATOR`: Uses a constructor, then calls any state-changing methods (including fluent setters like `withName()`).
    * `FIELD`: Uses a no-arg constructor and populates fields directly using reflection.
    * `BUILDER`: Uses a builder pattern (e.g., from Lombok or Immutables).
    * `STATIC_METHOD`: Uses a public static factory method to create an instance.

#### `randomValues`

Controls whether data is random or fixed.

* **Default**: `true` (random values)
* **Details**: When set to `false`, populating the same class twice will produce identical objects. Random values are generated within sensible
  ranges (e.g., dates are +/- 1 year from the current date).

#### `overridePopulates`

Provides **custom logic** for creating instances of specific classes. This is essential when a class requires values with a specific format, or if you
want to control the outcome for a specific class.

For example, imagine a class `MyUUID` whose constructor takes a `String` but requires it to be a valid UUID. Test-Populator would normally provide a
random string like `"fghjklmnpa"`, which would cause the `MyUUID` constructor to fail.

`overridePopulates` lets you "teach" the library how to correctly create these objects.

You can provide a lambda directly in the configuration:

```java
PopulateConfig populateConfig = PopulateConfig.builder()
        // Solves the problem by providing a correctly formatted string for MyUUID
        .overridePopulate(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
        // Also useful for setting specific values, like the current date
        .overridePopulate(LocalDate.class, LocalDate::now)
        // or setting all Strings to a random UUID
        .overridePopulate(String.class, () -> UUID.randomUUID().toString())
        .build();
```

Instead of a lambda, you can also implement the `OverridePopulate` interface for more complex cases.

#### `nullOnCircularDependency`

Handles circular dependencies (e.g., `ClassA` has a field of `ClassB`, and `ClassB` has a field of `ClassA`).

* **Default**: `false`
* **Details**: Enabling this will break the circular reference by setting the recurring object to null, preventing a StackOverflowError. Note that
  this adds a small performance overhead, as the library must keep track of every class it visits during the population process.

#### `accessNonPublicConstructors`

Allows the library to use private or protected constructors.

* **Default**: `false`

### Strategy-Specific Options

* **For `SETTER` strategy**:

    * `setterPrefixes`: A list of prefixes for setter methods. Default is `["set"]` (classic setter pattern). Use `[""]` to match any void method with one argument.

* **For `BUILDER` strategy**:

    * `builderPattern`: Specifies which builder library to use. Options are `CUSTOM`, `LOMBOK`, `IMMUTABLES`, `PROTOBUF`. Default is `CUSTOM`.
    * `builderMethod`: The name of the method that creates the builder instance (e.g., `"builder"`). Used for `CUSTOM` pattern.
    * `buildMethod`: The name of the method that builds the final object (e.g., `"build"`). Used for `CUSTOM` pattern.

* **For `MUTATOR` strategy**:

    * `constructorType`: The preferred constructor to use. Options are `NO_ARG`, `SMALLEST`, `LARGEST`. Default is `NO_ARG`.

* **For `STATIC_METHOD` strategy**:

    * `methodType`: The preferred static factory method to use. Options are `LARGEST` (most parameters), `SMALLEST` (fewest parameters), `SIMPLEST` (
      prioritizes methods with simple parameter types). Default is `LARGEST`.

### Other Options

* `blacklistedMethods` / `blacklistedFields`: A list of method or field names to skip during population. Useful for avoiding code coverage
  instrumentation fields like `$jacocoInit`.
* `objectFactoryEnabled` (Experimental): If `true`, generates Java source code for the populated object in the
  `target/generated-test-sources/test-populator/` directory. **Note**: This will not work if the `FIELD` strategy or `accessNonPublicConstructors` is
  enabled.

-----

## Usage Examples

### Simple Setup

For quick use with default settings, you can create a `PopulateFactory` directly.

```java
// Uses default configuration (random values, CONSTRUCTOR-first strategy)
MyClass myClass = new PopulateFactory().populate(MyClass.class);
```

### Global Setup for a Project

It's often useful to create a static helper class with a shared configuration for your entire test suite.

```java
public class TestPopulator {

    // 1. Define the configuration once
    private static final PopulateConfig POPULATE_CONFIG = PopulateConfig.builder()
            // Provide custom logic for creating MyUUID objects
            .overridePopulate(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
            // Always set LocalDate to the current date
            .overridePopulate(LocalDate.class, LocalDate::now)
            .build();

    // 2. Create a single factory instance
    private static final PopulateFactory POPULATE_FACTORY = new PopulateFactory(POPULATE_CONFIG);

    // 3. Create a static helper method for easy access in tests
    public static <T> T populate(Class<T> clazz) {
        return POPULATE_FACTORY.populate(clazz);
    }
}
```

**Usage in a test:**

```java
// Now you can easily populate any object with your custom rules
MyClass2 myClass2 = TestPopulator.populate(MyClass2.class);
```

### "Full Coverage" Setup

This example shows a configuration designed to handle a wide variety of class structures by enabling almost all features.

```java
private static final PopulateConfig FULL_CONFIG = PopulateConfig.builder()
        // Try all strategies, starting with the most specific (BUILDER)
        .strategyOrder(List.of(BUILDER, SETTER, MUTATOR, CONSTRUCTOR, STATIC_METHOD, FIELD))
        // Specify the builder pattern to use
        .builderPattern(LOMBOK)
        // Use random values for broader test coverage
        .randomValues(true)
        // Allow access to private constructors if no public ones are suitable
        .accessNonPublicConstructors(true)
        // Prevent infinite loops with circular dependencies
        .nullOnCircularDependency(true)
        // For the MUTATOR strategy, prefer the constructor with the most arguments
        .constructorType(LARGEST)
        // For the SETTER strategy, consider any void method with one arg a setter
        .setterPrefix("")
        // For STATIC_METHOD strategy, prioritizes method with simple parameter types  
        .methodType(MethodType.SIMPLEST)
        .build();
```