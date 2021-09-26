# test-populator

[![Maven Central](https://img.shields.io/maven-central/v/com.github.anhem/test-populator.svg)](https://search.maven.org/search?q=g:com.github.anhem%20a:test-populator)
[![Build Status](https://travis-ci.com/anhem/test-populator.svg?branch=main)](https://travis-ci.com/github/anhem/test-populator)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.anhem%3Atest-populator&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.anhem%3Atest-populator)

Populate java classes with fixed or random data using reflection. Facilitates the creation of objects in tests.

Doing this:

```java
MyClass myClass = new PopulateFactory().populate(MyClass.class);
```

With this:

```java
public class MyClass {

    private String stringValue;
    private List<ArbitraryEnum> listWithEnums;
    private InnerClass myInnerClass;

    public static class InnerClass {
        private int integer;
        private Map<String, LocalDate> stringToLocalDateMap;
    }
}
```

Results in this:

```
MyClass{
    stringValue='xksqbhddha', 
    listWithEnums=[B], 
    myInnerClass=InnerClass{
        integer=789707, 
        stringToLocalDateMap={dsyyjxizvp=2021-02-14}
    }
}
```

# Maven

```xml

<dependency>
    <groupId>com.github.anhem</groupId>
    <artifactId>test-populator</artifactId>
    <version>0.1.3</version>
    <scope>test</scope>
</dependency>
```

# Configuration

Use PopulateConfig to configure how test-populator should run.

| config | Values | Default
|---|---|---
| strategy | Any of CONSTRUCTOR,FIELD,SETTER,BUILDER | CONSTRUCTOR,FIELD
| builderPattern | LOMBOK / IMMUTABLES | -
| randomValues | true / false | true
| setterPrefix | prefix of setter methods | set
| overridePopulates | List of OverridePopulate implementations | -

### Strategy

There are a some different strategies used to populate, and they are being used in the order specified. I.e if the first
strategy is not suitable for populating, the next will be tried and so on.

##### CONSTRUCTOR

Use the constructor with most parameters to populate.

##### FIELD

Use the default constructor to instantiate and then use reflection to populate all the fields.

##### SETTER

Use the default constructor to instantiate and then setter methods to populate fields.

##### BUILDER

Use builders to populate. Supports [Lombok](https://projectlombok.org/) and [Immutables](https://immutables.github.io/).
Configured by setting [builderPattern](#builderpattern).

### BuilderPattern

(Applied when using strategy: BUILDER)

Different builders behave slightly different. The builderPattern tells test-populator which one to use.

### randomValues

Set to true will randomize everything. When set to false fixed values will be used. I.e. populating the same class
twice will give the same result.

### setterPrefix

(Applied when using strategy: SETTER)

Use setters with a different format than set*

### overridePopulates

Some classes might be difficult to populate automatically.

This class for example cannot be handled by test-populator alone:

```java
public class MyUUID {

    private final UUID uuid;

    public MyUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
}
```

It requires a String that can be converted into a UUID which will be impossible to accomplish using random strings. To
remedy this we can override population of this class by creating our own MyUUID populator class:

```java
public class MyUUIDOverride implements OverridePopulate<MyUUID> {

    @Override
    public MyUUID create() {
        return new MyUUID(UUID.randomUUID().toString());
    }
}
```

# Examples

## Simple setup

Simple setup using default configuration.

```java
MyClass myClass=new PopulateFactory().populate(MyClass.class);
```

## Global setup

Useful if you want to use the same configuration everywhere in your project.

### Setup

```java
public class TestPopulator {

    public static <T> T populate(Class<T> clazz) {
        return populateFactory.populate(clazz);
    }

    private static class MyUUIDOverride implements OverridePopulate<MyUUID> {

        @Override
        public MyUUID create() {
            return new MyUUID(UUID.randomUUID().toString());
        }
    }

    private static final PopulateConfig populateConfig = PopulateConfig.builder()
            .overridePopulate(List.of(new MyUUIDOverride()))
            .build();

    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}
```

### Usage

```java
MyClass2 myClass2=TestPopulator.populate(MyClass2.class);
```
