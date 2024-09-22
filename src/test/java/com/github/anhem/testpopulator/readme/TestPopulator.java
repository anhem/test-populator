package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.TypeSupplier;
import com.github.anhem.testpopulator.readme.model.MyUUID;

import java.util.UUID;

public class TestPopulator {

    public static <T> T populate(Class<T> clazz) {
        return populateFactory.populate(clazz);
    }

    private static class MyUUIDTypeSupplier implements TypeSupplier<MyUUID> {

        @Override
        public MyUUID get() {
            return new MyUUID(UUID.randomUUID().toString());
        }
    }

    private static final PopulateConfig populateConfig = PopulateConfig.builder()
            .typeSupplier(MyUUID.class, new MyUUIDTypeSupplier())
            .build();

    private static final PopulateFactory populateFactory = new PopulateFactory(populateConfig);
}
