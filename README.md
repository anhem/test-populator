# test-populator

[![Build Status](https://travis-ci.org/anhem/test-populator.svg?branch=main)](https://travis-ci.org/github/anhem/test-populator)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.anhem%3Atest-populator&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.anhem%3Atest-populator)

This library populates java classes with fixed or random data using reflection. It makes it easy to create objects in tests.

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

# Configuration

Use PopulateConfig to configure how object-populator should run. 

| config | Values | Default 
|---|---|---
| strategy | Any of CONSTRUCTOR,FIELD,SETTER,BUILDER | CONSTRUCTOR,FIELD
| builderPattern | LOMBOK / IMMUTABLES | -
| randomValues | true / false | true
| overridePopulates | List of OverridePopulate implementations | -

### Strategy

There are a some different strategies used to populate, and they are being used in the order specified. 
I.e if the first strategy is not suitable for populating, the next will be tried and so on.

##### BUILDER
Use builders to populate. Supports [Lombok](https://projectlombok.org/) and [Immutables](https://immutables.github.io/). Configured by setting [builderPattern](#builderpattern).

##### CONSTRUCTOR
Use the constructor with most parameters to populate.

##### FIELD
Use the default constructor to instantiate and then use reflection to populate all the fields.

##### SETTER
Use the default constructor to instantiate and then setter methods to populate fields.

### BuilderPattern
(Applied when using strategy: BUILDER)

Different builders behave slightly different. The builderPattern tells test-populator which one to use. 

### randomValues
When set to true will randomize everything. When set to false fixed values will be used. I.e. populating the same class twice will give the same result.

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
It requires a String that can be converted into a UUID which will be impossible to accomplish using random strings.
To remedy this we can override population of this class by creating our own MyUUID populator class:

```java
public class MyUUIDOverride implements OverridePopulate<MyUUID> {

    @Override
    public MyUUID create() {
        return new MyUUID(UUID.randomUUID().toString());
    }
}
```
