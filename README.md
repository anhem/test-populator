# test-populator

[![CI](https://github.com/anhem/test-populator/workflows/CI/badge.svg)](https://github.com/anhem/test-populator/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anhem_test-populator&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anhem_test-populator)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.anhem/test-populator.svg)](https://central.sonatype.com/artifact/com.github.anhem/test-populator/)
[![OpenJDK](https://img.shields.io/badge/OpenJDK-11-brightgreen)](https://github.com/anhem/test-populator/blob/main/pom.xml#L26)

Populate java classes with fixed or random data. Facilitates the creation of objects in tests.

Doing this:

```java
MyClass myClass = new PopulateFactory().populate(MyClass.class);
```

With this:

```java
public class MyClass {

    private final String stringValue;
    private final List<ArbitraryEnum> listWithEnums;
    private final InnerClass myInnerClass;

    public MyClass(String stringValue, List<ArbitraryEnum> listWithEnums, InnerClass myInnerClass) {
        this.stringValue = stringValue;
        this.listWithEnums = listWithEnums;
        this.myInnerClass = myInnerClass;
    }

    public static class InnerClass {
        private final int integer;
        private final Map<String, LocalDate> stringToLocalDateMap;

        public InnerClass(int integer, Map<String, LocalDate> stringToLocalDateMap) {
            this.integer = integer;
            this.stringToLocalDateMap = stringToLocalDateMap;
        }
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

# Configuration

Use `PopulateConfig` to configure how `test-populator` should run. Calling `populate()` without first providing a
`PopulateConfig` will result in `test-populator` using default configuration.

```java
//1. Configure
PopulateConfig populateConfig = PopulateConfig.builder()
        ...
        .build();

//2. Set up
PopulateFactory populateFactory = new PopulateFactory(populateConfig);

//3. Use
MyClass myClass = populateFactory.populate(myClass.class);
```

| config                      | Values                                   | Default                         |
|-----------------------------|------------------------------------------|---------------------------------|
| strategyOrder               | CONSTRUCTOR, SETTER, FIELD, BUILDER      | CONSTRUCTOR, SETTER             |
| builderPattern              | LOMBOK / IMMUTABLES                      | -                               |
| randomValues                | true / false                             | true                            |
| setterPrefix                | prefix of setter methods                 | set                             |
| accessNonPublicConstructors | true / false                             | false                           |
| overridePopulates           | List of OverridePopulate implementations | -                               |
| blacklistedMethods          | List of methods to skip if encountered   | $jacocoInit                     |
| blacklistedFields           | List of fields to skip if encountered    | \_\_$lineHits$\_\_, $jacocoData |    
| objectFactoryEnabled        | Experimental! true / false               | false                           |    

### strategyOrder

There are a some different strategies used to populate, and they are being used in the order specified. I.e if the first
strategy is not suitable for populating, the next will be tried and so on.

##### CONSTRUCTOR

Use the constructor with most parameters to populate. Applied to classes that have a constructor with at least one
argument.

##### SETTER

Use a no-arguments/default constructor to instantiate and setter methods to populate fields. Applied to classes that
only have a no-arguments/default constructor and at least one setter method.

##### FIELD

(This is recommended to only be use as a last resort. You should consider changing your Class to conform to any of the
other strategies first)

Use a no-arguments/default constructor to instantiate and then use reflection to populate all the fields. Applied to
classes that only have a no-arguments/default constructor.

##### BUILDER

Use builders to populate. Supports [Lombok](https://projectlombok.org/) and [Immutables](https://immutables.github.io/).
Configured by setting [builderPattern](#builderpattern). Applied to classes with a builder method.

### builderPattern

(Applied when using strategy: [BUILDER](#Builder))

Different builders behave slightly different. The builderPattern tells test-populator which one to use.

### randomValues

Set to true will randomize everything. When set to false fixed values will be used. I.e. populating the same class twice
will give the same result.

Random values are not generated entirely at random. They are generated to be random enough. For example date and time of
various
types are randomized from between `"now" minus 1 year` and `"now" plus 730 days (2 years)`.

See [RandomUtil.java](src/main/java/com/github/anhem/testpopulator/RandomUtil.java) for more details.

### setterPrefix

(Applied when using strategy: [SETTER](#Setter))

Use setters with a different format than set*

### accessNonPublicConstructors

(This is recommended to only be use as a last resort. You should consider changing your Class constructor to public
instead)

Controls whether to allow access to private constructors when populating.

### overridePopulates

This solves a couple of issues that may be encountered:

1. I want to generate my own value for a specific class
2. test-populator fails to generate a value for a specific class
3. I get `Failed to find type to create value for <Class>. Not implemented?`

Some classes might be difficult to populate automatically, or you may want to decide what value should be set for a
specific class.

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

This can then be added to our configuration and will be used whenever MyUUID is encountered. Also see [Setup](#Setup)

### blacklistedMethods

named methods in the list will be skipped if encountered. This is mostly a code coverage issue and should rarely be
needed otherwise.

### blacklistedFields

named fields in the list will be skipped if encountered. This is mostly a code coverage issue and should rarely be
needed otherwise.

### objectFactoryEnabled

Experimental!

This will result in populated objects to also be generated as java code
in `target/generated-test-sources/test-populator/`.
These files can then be copied into your project and used as any other java class.

This will not work when [FIELD](#Field) or [accessNonPublicConstructors](#accessNonPublicConstructors) is used because
they use reflection to override how class are accessed.

## ToBuilder

calling `toBuilder()` on a PopulateConfig object will convert it back to a builder, making it easy to make copies of a
configuration with slightly different settings.

# Examples

## Simple setup

Simple setup using default configuration.

```java
MyClass myClass = new PopulateFactory().populate(MyClass.class);
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
MyClass2 myClass2 = TestPopulator.populate(MyClass2.class);
```
