package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.readme.model.MyUUID;

import java.time.LocalDate;
import java.util.UUID;

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
            .overridePopulate(String.class, () -> UUID.randomUUID().toString()) //sets all string to random UUID's
            .build();

    //setup PopulateFactory with configuration
    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}
