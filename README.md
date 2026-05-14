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
* **Zero Runtime Dependencies:** Written in plain Java, this library is lightweight and has no external runtime dependencies, ensuring it won't
  introduce transitive dependency conflicts into your project.
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

*   **Default**: `CONSTRUCTOR`, `SETTER`, `STATIC_METHOD`
*   **Available Strategies**:
    *   `CONSTRUCTOR`: Uses the constructor with the most parameters.
    *   `SETTER`: Uses a no-arg constructor, then calls standard setter methods (e.g., `setName()`).
    *   `MUTATOR`: Uses a constructor, then calls any state-changing methods (including fluent setters like `withName()`).
    *   `FIELD`: Uses a no-arg constructor and populates fields directly using reflection.
    *   `BUILDER`: Uses a builder pattern (e.g., from Lombok or Immutables).
    *   `STATIC_METHOD`: Uses a public static factory method to create an instance.

> [!TIP]
> **Strategy Best Practices**: While you can customize the `strategyOrder`, it is highly recommended to at least include `CONSTRUCTOR` and `STATIC_METHOD` (included in the defaults). 
> 
> Many strategies (like `SETTER` and `FIELD`) require a **no-arg constructor** to instantiate the object. If a class (or any of its nested dependencies) only has parameterized constructors or static factory methods, population will fail if the corresponding strategies are disabled.
>
> For a detailed look at how restrictive strategy configurations can lead to failures, see [StrategyFailureTest.java](src/test/java/com/github/anhem/testpopulator/readme/StrategyFailureTest.java).
>
> **Why Order Matters**: If multiple strategies match a class, the first one in the `strategyOrder` is used. If that strategy fails (e.g., it picks a constructor whose arguments cannot be populated), the entire population for that object fails—it does **not** fall back to the next strategy in the list. Reordering can be useful if you prefer one instantiation method over another or if one matches but is problematic. See [StrategyOrderTest.java](src/test/java/com/github/anhem/testpopulator/readme/StrategyOrderTest.java) for an example of how reordering can resolve population failures.

#### `randomValues`

Controls whether data is random or fixed.

* **Default**: `true` (random values)
* **Details**: When set to `false`, populating the same class twice will produce identical objects. Random values are generated within sensible
  ranges (e.g., dates are +/- 1 year from the current date).

#### `classOverrides`

Provides **custom logic** for creating instances of specific classes. This is essential when a class requires values with a specific format, or if you
want to control the outcome for a specific class.

For example, imagine a class `MyUUID` whose constructor takes a `String` but requires it to be a valid UUID. Test-Populator would normally provide a
random string like `"fghjklmnpa"`, which would cause the `MyUUID` constructor to fail.

`classOverrides` lets you "teach" the library how to correctly create these objects.

You can provide a lambda directly in the configuration:

```java
PopulateConfig populateConfig = PopulateConfig.builder()
        // Solves the problem by providing a correctly formatted string for MyUUID
        .addOverride(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
        // Also useful for setting specific values, like the current date
        .addOverride(LocalDate.class, LocalDate::now)
        // or setting all Strings to a random UUID
        .addOverride(String.class, () -> UUID.randomUUID().toString())
        .build();
```

Instead of a lambda, you can also implement the `OverridePopulate` interface for more complex cases. This is particularly useful when using the experimental **Java Code Generation** feature, as it allows you to define helper methods and their required imports that will be included in the generated source code.

```java
PopulateConfig populateConfig = PopulateConfig.builder()
        .addOverride(URL.class, new OverridePopulate<URL>() {
            @Override
            public URL create() {
                // Implementation for runtime population
                try {
                    return new URL("http://example.com");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String createCode() {
                // String used in generated code to call the helper method below
                return "toUrl(\"http://example.com\")";
            }

            @Override
            public Set<String> createMethods() {
                // Custom helper method definition to be added to the generated class
                return Set.of(
                    "\tprivate static java.net.URL toUrl(String url) {\n" +
                    "\t\ttry {\n" +
                    "\t\t\treturn new java.net.URL(url);\n" +
                    "\t\t} catch (java.net.MalformedURLException e) {\n" +
                    "\t\t\tthrow new RuntimeException(e);\n" +
                    "\t\t}\n" +
                    "\t}"
                );
            }

            @Override
            public Set<String> createImports() {
                // Additional imports required for the generated class
                return Set.of("java.net.URL", "java.net.MalformedURLException");
            }
        })
        .build();
```

#### `nameOverrides`

Similar to `classOverrides`, but allows you to provide custom logic based on the **name of the field or method parameter**, and its **expected class type**.

This is particularly useful when you have multiple fields of the same type but want to assign them different values or if you want to target a specific field across multiple classes.

```java
PopulateConfig populateConfig = PopulateConfig.builder()
        // Targets a field named 'fromDate' of type LocalDate
        .addOverride("fromDate", LocalDate.class, () -> LocalDate.of(2021, 1, 1))
        // Targets a setter method named 'setFromDate' of type LocalDate
        .addOverride("setFromDate", LocalDate.class, () -> LocalDate.of(2021, 1, 1))
        .build();
```

`nameOverrides` takes precedence over `classOverrides`. Name-based overrides are fully type-safe; an override is only applied if both the name and the expected type match exactly. If no match is found, the library falls back to class overrides or default population logic.

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
            .addOverride(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString()))
            // Always set LocalDate to the current date
            .addOverride(LocalDate.class, LocalDate::now)
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

## Technical Insights

Test-Populator is built on a few core architectural principles that ensure its flexibility and reliability:

*   **Recursive Strategy-Based Engine**: The library uses a depth-first traversal of the object graph. For each type, it evaluates a pluggable chain of strategies (Constructor, Builder, Setter, etc.) to find the most suitable instantiation method.
*   **Immutable Context (Carriers)**: All state during the recursive population is passed through immutable "Carriers". This ensures that the population process is deterministic and free of side effects across different branches of the object tree.
*   **Decoupled Code Generation**: The experimental Java code generation feature is fully decoupled from the population logic. It "records" the population steps via a notification interface, allowing the library to build the object in memory while simultaneously constructing a mirror tree of `ObjectBuilder` nodes for code output.
*   **Round-Trip Validation**: The library's own test suite uses a unique "compile-and-run" strategy for its generated code. It writes the generated Java source to a file, compiles it on the fly, instantiates the resulting class, and performs a recursive comparison against the original object to ensure 100% behavioral parity.

## Thread Safety

Test-Populator is designed to be **thread-safe** and can be used concurrently in multi-threaded test environments.

*   **`PopulateFactory` is stateless**: A single factory instance can be safely shared across multiple threads.
*   **Immutable Configuration**: `PopulateConfig` is effectively immutable after construction.
*   **Thread-Confined Recursion**: Each call to `populate()` creates its own localized context (Carriers and ObjectFactory), ensuring no shared state between concurrent requests.
*   **Safe Randomization**: The internal random data generation uses `java.security.SecureRandom`, which is thread-safe.

You can confidently use a single `PopulateFactory` instance as a singleton or static constant across your entire test suite.

## Building from Source

### Prerequisites
- JDK 11 or higher
- Maven 3.6.3 or higher

### Commands
- **Build and Install**: `mvn clean install`
- **Run Tests**: `mvn test`
- **Check Coverage**: `mvn -Psonar clean test jacoco:report` (Reports generated in `target/site/jacoco/`)