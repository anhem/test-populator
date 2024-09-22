package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.typesupplier.MyUUID;
import com.github.anhem.testpopulator.model.java.typesupplier.MyUUIDTypeSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithTypeSuppliersTest {

    private PopulateFactory populateFactory;
    private PopulateConfig populateConfig;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .typeSupplier(MyUUID.class, new MyUUIDTypeSupplier())
                .objectFactoryEnabled(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void myUUID() {
        MyUUID value_1 = populateAndAssertWithGeneratedCode(MyUUID.class);
        MyUUID value_2 = populateAndAssertWithGeneratedCode(MyUUID.class);

        assertThat(value_1).usingRecursiveAssertion().isEqualTo(value_2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }
}
