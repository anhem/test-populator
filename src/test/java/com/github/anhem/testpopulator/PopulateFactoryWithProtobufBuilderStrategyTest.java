package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.proto.complex.UserProfile;
import com.github.anhem.testpopulator.model.proto.simple.Person;
import com.github.anhem.testpopulator.model.proto.wrappers.Wrappers;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.UInt32Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.BuilderPattern.PROTOBUF;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

public class PopulateFactoryWithProtobufBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(PROTOBUF)
                .objectFactoryEnabled(true)
                .methodType(MethodType.SIMPLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void int32Value() {
        Int32Value value1 = populateAndAssertWithGeneratedCode(Int32Value.class);
        Int32Value value2 = populateAndAssertWithGeneratedCode(Int32Value.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void uInt32Value() {
        UInt32Value value1 = populateAndAssertWithGeneratedCode(UInt32Value.class);
        UInt32Value value2 = populateAndAssertWithGeneratedCode(UInt32Value.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void byteString() {
        ByteString value1 = populateAndAssertWithGeneratedCode(ByteString.class);
        ByteString value2 = populateAndAssertWithGeneratedCode(ByteString.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void bytesValue() {
        BytesValue value1 = populateAndAssertWithGeneratedCode(BytesValue.class);
        BytesValue value2 = populateAndAssertWithGeneratedCode(BytesValue.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void simple() {
        Person value1 = populateAndAssertWithGeneratedCode(Person.class);
        Person value2 = populateAndAssertWithGeneratedCode(Person.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void wrappers() {
        Wrappers.AllWrappers.newBuilder().setInt32Value(Int32Value.newBuilder().setValue(42).build());
        Wrappers.AllWrappers value1 = populateAndAssertWithGeneratedCode(Wrappers.AllWrappers.class);
        Wrappers.AllWrappers value2 = populateAndAssertWithGeneratedCode(Wrappers.AllWrappers.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void complex() {
        UserProfile value1 = populateAndAssertWithGeneratedCode(UserProfile.class);
        UserProfile value2 = populateAndAssertWithGeneratedCode(UserProfile.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(PROTOBUF);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(clazz, value, populateConfig);

        return value;
    }

    public static <T> void assertRandomlyPopulatedValues(T value1, T value2) {
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1).hasNoNullFieldsOrProperties();
        assertThat(value2).hasNoNullFieldsOrProperties();
        assertThat(value1).isNotEqualTo(value2);
        assertThat(value1).usingRecursiveAssertion().isNotEqualTo(value2);
    }
}
