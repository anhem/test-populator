# Changelog

All notable changes to this project will be documented in this file.

## [1.0.1] - 2026-05-18

### Configuration (PopulateConfig)
- **API Consolidation:** Completed the migration to nested sub-builders. Redundant top-level helper methods (e.g., `builderPattern()`, `setterPrefixes()`, `objectFactoryEnabled()`) have been removed to enforce a single, consistent way of configuring strategies.
- **Implicit Strategy Registration:** Calling a sub-builder (e.g., `.builderStrategy()`) now automatically registers that strategy in the `strategyOrder` if it's not already present.
- **Builder Reset:** Added `.reset()` to `BuilderConfig` allowing users to revert builder-specific settings (like method names) to their pattern-defaults (e.g., switching from custom names back to Protobuf defaults).
- **Robust Copying:** Enhanced `toBuilder()` to perform a more direct and reliable copy of the internal configuration state.

## [1.0.0] - 2026-05-17

### Major Features
- **Native Kotlin Support:** Automatically handles Kotlin classes, including those with default parameter values and complex generic collections, by intelligently mapping synthetic constructors back to primary metadata.
- **Protobuf Support:** Seamlessly populates Protobuf messages using their generated builders and `newBuilder()`/`build()` pattern.
- **Unified Execution Overrides:** The `populate` method in `PopulateFactory` now accepts a unified map of overrides (keys can be `Class` or `OverrideTarget`) or single convenience overloads, scoped specifically to that call.
- **Type-Safe Name Overrides:** Introduced `OverrideTarget` for overriding specific fields/methods by name and type. This replaces the unsafe string-key pattern, ensuring overrides are applied only where intended.
- **Experimental Code Generation:** Refactored the `ObjectFactory` to generate clean, compilable Java source code for populated objects, supporting round-trip validation.

### Configuration (PopulateConfig)
- **Fluent Nested Builders:** Refactored the builder API to use specialized sub-builders (`KotlinSupport`, `ObjectFactoryConfig`, `SetterConfig`, etc.) for better discoverability and readability.
- **Build System Auto-Detection:** Added automatic detection of Maven and Gradle environments to set sensible defaults for generated source paths (`target/` vs `build/`).
- **Standardized API:**
    - Standardized boolean getters (using `is` prefix) and builder methods (requiring explicit parameters).
    - Added comprehensive `clear` methods to the builder to reset strategy orders, blacklists, and overrides.
    - Simplified builder and build method name resolution, allowing overrides for all patterns including `LOMBOK`.

### Expanded JDK Type Support
- **Value Types:** Added support for `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `StringBuilder`, `StringBuffer`, `UUID`, `Currency`, `Locale`, `TimeZone`, `ZoneId`, `ZoneOffset`, `Year`, `YearMonth`, `MonthDay`, `Month`, `DayOfWeek`.
- **I/O & Networking:** Added support for `Path`, `Charset`, `ByteBuffer`, `InetAddress`, `Inet4Address`, `Inet6Address`, `InetSocketAddress`, `URL`, `URI`.
- **Functional & Concurrency:** Added support for `Optional`, `OptionalInt`, `OptionalLong`, `OptionalDouble`, `Future`, `CompletableFuture`, `Stream`, `IntStream`, `LongStream`, `DoubleStream`.
- **Collections & Iteration:** Added support for `Scanner`, `Iterator`, `Iterable`, `EnumSet`, `EnumMap`, and specialized handling for `ConcurrentMap` and `NavigableMap`.

### Internal Improvements
- **Robust Tree Traversal:** Implemented an `expectedChildren` count mechanism in `ObjectFactory` to ensure accurate mirror-tree construction during deep population.
- **Kotlin Metadata Resolution:** Added logic to resolve primary constructor metadata when invoking synthetic Kotlin constructors to preserve generic type information.

### Removed
- **Legacy Builders:** Removed `BuildTypeObjectBuilder`, `BuilderObjectBuilder`, `MethodObjectBuilder`, and `StaticMethodObjectBuilder` in favor of a unified template system.
- **Unsafe Patterns:** Removed raw `String` key support in local overrides to enforce strict type safety and prevent runtime errors.
- **Deprecated API:** Removed all v1.x methods marked as `@Deprecated`.

---

# Migration Guide: v0.1.x to v1.0.0

This guide assists in migrating from Test-Populator v0.x to v1.0.0. This release includes significant API refactoring to improve discoverability and type safety.

## Breaking Changes

### 1. PopulateConfig Builder API Refactor

The top-level configuration methods for specific strategies have been moved into nested builders.

**v0.1.x:**
```java
PopulateConfig config = PopulateConfig.builder()
    .builderPattern(BuilderPattern.LOMBOK)
    .setterPrefixes("with")
    .strategyOrder(Strategy.CONSTRUCTOR, Strategy.SETTER)
    .build();
```

**v1.0.0:**
```java
PopulateConfig config = PopulateConfig.builder()
    .builderStrategy()
        .pattern(BuilderPattern.LOMBOK)
        .and()
    .setterStrategy()
        .setPrefixes("with")
        .and()
    .reorderStrategies(Strategy.CONSTRUCTOR, Strategy.SETTER)
    .build();
```

### 2. Method Renames and Signature Changes

- `PopulateConfig.getOverridePopulate()` -> `getClassOverrides()`.
- `PopulateConfig.getStrategyOrder()` still exists, but the builder method `strategyOrder(List<Strategy>)` and `strategyOrder(Strategy)` have been replaced by `reorderStrategies(Strategy...)`.
- Configuration methods for blacklists and prefixes that previously used `List<String>` now use `Set<String>`.
- `blacklistedMethods(List<String>)` -> `setBlacklistedMethods(Collection<String>)` or `setBlacklistedMethods(String...)`.
- `blacklistedFields(List<String>)` -> `setBlacklistedFields(Collection<String>)` or `setBlacklistedFields(String...)`.

### 3. Removal of Deprecated Methods

All methods previously marked as `@Deprecated` in v0.x have been removed. This includes:
- `PopulateConfig.Builder.addBlacklistedMethod(String)` (use `addBlacklistedMethods`)
- `PopulateConfig.Builder.addBlacklistedField(String)` (use `addBlacklistedFields`)
- `PopulateConfig.Builder.setterPrefix(String)` (use `setterStrategy().addPrefixes()`)

### 4. ObjectFactory Configuration

The configuration for the experimental `ObjectFactory` has been moved to a sub-builder.

**v0.x:**
```java
PopulateConfig config = PopulateConfig.builder()
    .objectFactoryEnabled(true)
    .objectFactoryPath("custom/path")
    .build();
```

**v1.0.0:**
```java
PopulateConfig config = PopulateConfig.builder()
    .objectFactory(true)
        .path("custom/path")
        .and()
    .build();
```

## New Features and Improvements

### Local Overrides in `PopulateFactory`

You can now pass overrides directly to the `populate` method using convenience methods. These overrides are scoped to the current execution and take precedence over global configuration.

```java
PopulateFactory factory = new PopulateFactory();

// 1. Convenience method for a single class override
MyClass obj = factory.populate(MyClass.class, String.class, () -> "local-value");

// 2. Convenience method for a single name + type override
MyClass obj = factory.populate(MyClass.class, "id", UUID.class, () -> UUID.randomUUID());

// 3. For multiple overrides, pass specialized maps
Map<Class<?>, OverridePopulate<?>> classOverrides = Map.of(Integer.class, () -> 42);
Map<OverrideTarget, OverridePopulate<?>> nameOverrides = Map.of(OverrideTarget.of("email", String.class), () -> "test@example.com");

// Either individually
MyClass obj1 = factory.populate(MyClass.class, classOverrides);
MyClass obj2 = factory.populate(MyClass.class, nameOverrides);

// Or both (using Map)
Map<Object, OverridePopulate<?>> mixedOverrides = Map.of(
    Integer.class, () -> 42,
    OverrideTarget.of("email", String.class), () -> "test@example.com"
);
MyClass obj3 = factory.populate(MyClass.class, mixedOverrides);
```

> **Note:** The `populate` method is now strictly type-safe. Using an unsupported key type (like a raw `String`) in the overrides map will result in an `IllegalArgumentException`.

### Type-Safe Name Overrides

Name-based overrides now require a target class to prevent accidental matches across different types.

```java
PopulateConfig config = PopulateConfig.builder()
    .addOverride("id", UUID.class, () -> UUID.nameUUIDFromBytes("test".getBytes()))
    .build();
```

### Kotlin Support

Native support for Kotlin default values can now be enabled via the builder:

```java
PopulateConfig config = PopulateConfig.builder()
    .kotlinSupport(true)
        .defaultValues(true)
        .and()
    .build();
```

Alternatively, you can enable it without default values:

```java
PopulateConfig config = PopulateConfig.builder()
    .kotlinSupport(true)
    .build();
```

### Expanded JDK Type Support

Many JDK types that previously required custom overrides are now supported out-of-the-box, including `Optional`, `Stream`, `Path`, `InetAddress`, `Currency`, and `Locale`. Check the `CHANGELOG.md` for the full list.

## Internal Refactoring (ObjectFactory)

If you have implemented custom `ObjectBuilder` classes for the experimental code generation feature:
- The base class `ObjectBuilder` has changed significantly.
- Legacy specific builders (`BuildTypeObjectBuilder`, etc.) have been removed in favor of `TemplateObjectBuilder`.
- Refer to the new `CodeTemplate` enum for the supported formatting patterns.
