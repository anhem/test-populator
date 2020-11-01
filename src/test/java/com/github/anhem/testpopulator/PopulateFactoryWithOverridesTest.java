package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.testutil.AssertUtil.assertRandomlyPopulatedValues;

class PopulateFactoryWithOverridesTest {

    PopulateFactory populateFactory;

    @BeforeEach
    public void setUp() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .overridePopulate(List.of(new MyUUIDOverride()))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    public void myUUID() {
        MyUUID value_1 = populateFactory.populate(MyUUID.class);
        MyUUID value_2 = populateFactory.populate(MyUUID.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }


}
