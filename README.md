# test-populator

[![CI](https://github.com/anhem/test-populator/workflows/CI/badge.svg)](https://github.com/anhem/test-populator/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anhem_test-populator&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anhem_test-populator)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.anhem/test-populator.svg)](https://central.sonatype.com/artifact/com.github.anhem/test-populator/)
[![OpenJDK](https://img.shields.io/badge/OpenJDK-11-brightgreen)](https://github.com/anhem/test-populator/blob/main/pom.xml#L26)

Populate java classes with fixed or random data. Facilitates the creation of objects in tests.

**Problem:**

When writing tests, you may not always be interested in what data an object has. You just want to make use of an object of that type with some or all
fields
populated.

Creating a lot of objects manually is time-consuming and gives you additional code to maintain.

The same goes for mocking objects. It works, but can also result in a huge and complicated mocking setup that also needs to be maintained.

**Solution:**

`test-populator` solves the hassle of setting up test data by automatically creating objects for you; in the same way these objects would normally get
created.

**Doing this:**

```java
MyClass myClass = new PopulateFactory().populate(MyClass.class);
```

**With this:**

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

**Results in this:**

```java
//output from toString()
MyClass {
    stringValue = 'xksqbhddha',
            listWithEnums =[B],
    myInnerClass = InnerClass {
        integer = 789707,
                stringToLocalDateMap = {dsyyjxizvp = 2021 - 02 - 14}
    }
}
```

# Configuration

Use `PopulateConfig` to configure how `test-populator` should run.

Calling `populate()` without first providing a `PopulateConfig` will result in `test-populator` using default configuration.

```java
//1. Configure
PopulateConfig populateConfig = PopulateConfig.builder()
        ...
                .

build();

//2. Set up
PopulateFactory populateFactory = new PopulateFactory(populateConfig);

//3. Use
MyClass myClass = populateFactory.populate(myClass.class);
```

| config                      | Values                                                      | Default                            |
|-----------------------------|-------------------------------------------------------------|------------------------------------|
| strategyOrder               | CONSTRUCTOR, SETTER, MUTATOR, FIELD, BUILDER, STATIC_METHOD | CONSTRUCTOR, SETTER, STATIC_METHOD |
| builderPattern              | CUSTOM / LOMBOK / IMMUTABLES                                | CUSTOM                             |
| randomValues                | true / false                                                | true                               |
| setterPrefixes              | prefix of setter methods                                    | set                                |
| accessNonPublicConstructors | true / false                                                | false                              |
| overridePopulates           | List of OverridePopulate implementations                    | -                                  |
| blacklistedMethods          | List of methods to skip if encountered                      | $jacocoInit                        |
| blacklistedFields           | List of fields to skip if encountered                       | \_\_$lineHits$\_\_, $jacocoData    |    
| objectFactoryEnabled        | Experimental! true / false                                  | false                              |    
| nullOnCircularDependency    | true / false                                                | false                              |    
| constructorType             | NO_ARG, SMALLEST, LARGEST                                   | NO_ARG                             |    
| builderMethod               | name of builder method                                      | builder                            |    
| buildMethod                 | name of build method                                        | build                              |    

### strategyOrder

There are a some different strategies used to populate, and they are being used in the order specified. I.e if the first
strategy is not suitable for populating, the next will be tried and so on.

##### CONSTRUCTOR

Use the constructor with most parameters to populate. Applied to classes that have a constructor with at least one
argument.

##### SETTER

Use a no-arguments/default constructor to instantiate and setter methods to populate fields. Applied to classes that
have a no-arguments/default constructor and at least one setter method.

This works similarly to [MUTATOR](#mutator) but will take methods that follow the classic setter pattern.

Methods are considered setters if they match any of the provided [setterPrefixes](#setterprefixes), with `one argument` and return `void`.

##### MUTATOR

Use a constructor based on [constructorType](#constructortype) to instantiate and mutator methods to populate fields. Applied to classes that
have at least one mutator method.

This works similarly to [SETTER](#setter) but will take any method that mutates the object instead of only those following the classic setter pattern.

Methods are considered mutators if they have at least `one argument` and return `void` or the class `test-populator` is currently creating.

##### FIELD

Use a no-arguments/default constructor to instantiate and then use reflection to populate all the fields. Applied to
classes that have a no-arguments/default constructor.

##### BUILDER

Use builders to populate. Supports [Lombok](https://projectlombok.org/) and [Immutables](https://immutables.github.io/) as well as a lightly
customizable variant `CUSTOM` where builder and build methods can be defined.
Configured by setting [builderPattern](#builderpattern). Applied to classes with a builder method.

##### STATIC_METHOD

Use a public static method that returns an object of the class to populate. The method with most parameters will be used if there are more than one
method matching this criteria.

### builderPattern

(Applied when using strategy: [BUILDER](#Builder))

Different builders behave slightly different. The builderPattern tells `test-populator` which one to use.

### randomValues

Set to true will randomize everything. When set to false fixed values will be used. I.e. populating the same class twice
will give the same result.

**Note!** Random values are not generated entirely at random. They are generated to be random enough. For example date and time of
various types are randomized from between `"now" minus 1 year` and `"now" plus 1 year`.

See [RandomUtil.java](src/main/java/com/github/anhem/testpopulator/RandomUtil.java) for more details.

### setterPrefixes

(Applied when using strategy: [SETTER](#Setter))

Use setters with a different format than `set*`

An empty string `""` can be used to make use of any void method with one argument

### accessNonPublicConstructors

Controls whether to allow access to private or protected constructors when populating.

### overridePopulates

This solves a couple of issues that may be encountered:

1. I want to generate my own value for a specific class
2. `test-populator` fails to generate a value for a specific class
3. I get `Failed to find type to create value for <Class>. Not implemented?`

You can provide your own values by creating your own classes implementing the `OverridePopulate` interface (see the `MyUUID` example below),
or by simply providing your own implementation directly in the configuration:

```java
    PopulateConfig populateConfig = PopulateConfig.builder()
        .overridePopulate(LocalDate.class, LocalDate::now) //set all LocalDates to "now"
        .overridePopulate(String.class, () -> UUID.randomUUID().toString()) //set all strings to random UUID's
        .build();
```

Some classes might be difficult to populate automatically, or you may want to decide what value should be set for a
specific class.

This class for example cannot be handled by `test-populator` alone:

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

This can then be added to our configuration and will be used whenever MyUUID is encountered.

```java
    PopulateConfig populateConfig = PopulateConfig.builder()
        .overridePopulate(MyUUID.class, new MyUUIDOverridePopulate()) //provides own implementation of how to create MyUUID
        .build();
```

Also see [Setup](#Setup) for a complete example

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

### nullOnCircularDependency

Enable to solve issues with classes having circular dependencies. In cases where circular dependencies exists you will experience a
`StackOverflowError`.

By enabling this the circle is broken by setting those values to `null`.

### constructorType

(Applied when using strategy: [MUTATOR](#Mutator))

Set what constructor is preferred when creating objects.

SMALLEST will attempt to pick a constructor with at least one parameter and fall back on NO_ARG if none is found.

### builderMethod

(Applied when using [builderPattern](#BuilderPattern): `CUSTOM`)

Set the name of the builder method used when creating objects using BUILDER strategy. This option will be ignored for `LOMBOK` and `IMMUTABLES`.

### buildMethod

(Applied when using [builderPattern](#BuilderPattern): `CUSTOM`)

Set the name of the build method used when creating objects using BUILDER strategy. This option will be ignored for `LOMBOK` and `IMMUTABLES`.

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

**Setup:**

```java
public class TestPopulator {

    //static method accessible everywhere in our tests
    public static <T> T populate(Class<T> clazz) {
        return populateFactory.populate(clazz);
    }

    //own implementation of how to create MyUUID objects
    private static class MyUUIDOverridePopulate implements OverridePopulate<MyUUID> {

        @Override
        public MyUUID create() {
            return new MyUUID(UUID.randomUUID().toString());
        }

        //Only necessary if ObjectFactory is used, can be ignored otherwise. ObjectFactory is not enabled by default.
        //This provides ObjectFactory with a string used to generate Java code for MyUUID.
        @Override
        public String createString() {
            return "new MyUUID(java.util.UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\").toString())";
        }
    }

    //configuration
    private static final PopulateConfig populateConfig = PopulateConfig.builder()
            .overridePopulate(MyUUID.class, new MyUUIDOverridePopulate()) //provides own implementation of how to create MyUUID
            .overridePopulate(LocalDate.class, LocalDate::now) //set all LocalDates to "now"
            .overridePopulate(String.class, () -> UUID.randomUUID().toString()) //sets all strings to random UUID's
            .build();

    //setup PopulateFactory with configuration
    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}
```

**Usage:**

```java
MyClass2 myClass2 = TestPopulator.populate(MyClass2.class);
```

## Full coverage setup

Here is a setup that covers as much as possible

```java
public class TestPopulator {

    public static <T> T populate(Class<T> clazz) {
        return populateFactory.populate(clazz);
    }

    private static final PopulateConfig populateConfig = PopulateConfig.builder()
            .strategyOrder(BUILDER, SETTER, MUTATOR, CONSTRUCTOR, STATIC_METHOD, FIELD) // strategies ordered to make most use of each of them
            .builderPattern(LOMBOK) // required when using BUILDER strategy to tell test-populator what kind of builder to use 
            .randomValues(true) // create objects with random values
            .setterPrefix("") // used by SETTER strategy to know what methods to use. An empty string means calling all void methods with one argument
            .accessNonPublicConstructors(true) // uses reflection to override access to private constructors by calling constructor.setAccessible(true)
            .overridePopulate(MyUUID.class, () -> new MyUUID(UUID.randomUUID().toString())) // if we have our own custom class that test-populator cannot populate without help
            .objectFactoryEnabled(false) // generates java code for each populated object. Must be false if FIELD strategy or accessNonPublicConstructors = true is used
            .nullOnCircularDependency(true) // solves issues with circular dependencies by setting values to null when encountered more than once
            .constructorType(LARGEST) // used by MUTATOR strategy to know what constructor to use
            .build();

    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}
```