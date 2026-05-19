# Changelog

All notable changes to this project will be documented in this file.

## [1.0.1] - 2026-05-18

### Configuration (PopulateConfig)
- **API Consolidation:** Completed the migration to nested sub-builders. Redundant top-level helper methods (e.g., `builderPattern()`, `setterPrefixes()`, `objectFactoryEnabled()`) have been removed to enforce a single, consistent way of configuring strategies.
- **Implicit Strategy Registration:** Calling a sub-builder (e.g., `.builderStrategy()`) now automatically registers that strategy in the `strategyOrder` if it's not already present.
- **Builder Reset:** Added `.reset()` to `BuilderConfig` allowing users to revert builder-specific settings (like method names) to their pattern-defaults (e.g., switching from custom names back to Protobuf defaults).
- **Robust Copying:** Enhanced `toBuilder()` to perform a more direct and reliable copy of the internal configuration state.

## [1.0.0] - 2026-05-17

> **Note:** For migration instructions, please refer to the [Migration Guide: v1.0.1](#migration-guide-v101).

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

# Migration Guide: v1.0.1

This guide assists in migrating from Test-Populator v0.x or v1.0.0 to v1.0.1. This release finalizes the configuration API consolidation and enforces strict usage of nested sub-builders.

## Breaking Changes

### 1. Final Removal of Top-Level Configuration Helpers

Redundant top-level helper methods in `PopulateConfig.Builder` have been removed. You must now use the corresponding sub-builders.

**v1.0.0 (Deprecated/Redundant):**
```java
PopulateConfig config = PopulateConfig.builder()
    .builderPattern(BuilderPattern.LOMBOK)
    .setterPrefixes("with")
    .objectFactoryEnabled(true)
    .build();
```

**v1.0.1 (Required):**
```java
PopulateConfig config = PopulateConfig.builder()
    .builderStrategy()
        .pattern(BuilderPattern.LOMBOK)
        .and()
    .setterStrategy()
        .setPrefixes("with")
        .and()
    .objectFactory(true)
        .and()
    .build();
```

The following methods have been removed from `PopulateConfig.Builder`:
- `builderPattern(BuilderPattern)` -> use `builderStrategy().pattern(BuilderPattern)`
- `setterPrefixes(String...)` -> use `setterStrategy().setPrefixes(String...)`
- `setterPrefixes(Collection<String>)` -> use `setterStrategy().setPrefixes(Collection<String>)`
- `objectFactoryEnabled(boolean)` -> use `objectFactory(boolean)`
- `objectFactoryPath(String)` -> use `objectFactory(true).path(String)`
- `objectFactoryWriteToFile(boolean)` -> use `objectFactory(true).writeToFile(boolean)`

### 2. Method Renames and Signature Changes

- `PopulateConfig.getOverridePopulate()` -> `getClassOverrides()`.
- Local overrides in `PopulateFactory.populate()` now require strict type safety. Passing a raw `String` as a key in the overrides map is no longer supported; use `OverrideTarget` for name-based overrides.
- Configuration methods for blacklists and prefixes that previously used `List<String>` now consistently use `Set<String>` internally, though builder methods often accept `Collection` or varargs.

## New Features and Improvements

### 1. Implicit Strategy Registration

Calling a sub-builder (e.g., `.builderStrategy()`, `.setterStrategy()`) now automatically registers that strategy in the `strategyOrder`. You only need to call `reorderStrategies()` if you need a specific priority other than the order in which they were configured.

### 2. Builder Reset

You can now reset builder-specific naming configurations to their pattern defaults:
```java
PopulateConfig config = PopulateConfig.builder()
    .builderStrategy()
        .setBuilderMethodName("custom")
        .reset() // Reverts builderMethodName to the default for the current pattern
        .and()
    .build();
```

### 3. Local Overrides in `PopulateFactory`

The `populate` method allows applying call-scoped overrides that take precedence over global configuration:

```java
PopulateFactory factory = new PopulateFactory();

// 1. Single class override
MyClass obj = factory.populate(MyClass.class, String.class, () -> "local-value");

// 2. Name + type override
MyClass obj = factory.populate(MyClass.class, "id", UUID.class, () -> UUID.randomUUID());

// 3. Mixed overrides using a Map
Map<Object, OverridePopulate<?>> overrides = Map.of(
    Integer.class, () -> 42,
    OverrideTarget.of("email", String.class), () -> "test@example.com"
);
MyClass obj = factory.populate(MyClass.class, overrides);
```

### 4. Kotlin Support

Native support for Kotlin classes, including default parameter values, can be enabled via the builder:

```java
PopulateConfig config = PopulateConfig.builder()
    .kotlinSupport(true)
        .defaultValues(true)
        .and()
    .build();
```
