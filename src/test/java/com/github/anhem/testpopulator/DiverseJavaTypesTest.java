package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.DiverseJavaTypes;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

public class DiverseJavaTypesTest {

    private PopulateConfig populateConfig;

    @Test
    void setterStrategy() {
        populateConfig = PopulateConfig.builder()
                .setterStrategy()
                .and()
                .objectFactoryEnabled(true)
                .build();
        populateAndAssert(populateConfig);
    }

    @Test
    void constructorStrategy() {
        populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                .and()
                .objectFactoryEnabled(true)
                .build();
        populateAndAssert(populateConfig);
    }

    @Test
    void fieldStrategy() {
        populateConfig = PopulateConfig.builder()
                .fieldStrategy()
                .and()
                .build();
        populateAndAssert(populateConfig);
    }

    @Test
    void mutatorStrategy() {
        populateConfig = PopulateConfig.builder()
                .mutatorStrategy()
                .and()
                .objectFactoryEnabled(true)
                .build();
        populateAndAssert(populateConfig);
    }

    @Test
    void staticMethodStrategy() {
        populateConfig = PopulateConfig.builder()
                .staticMethodStrategy()
                .and()
                .objectFactoryEnabled(true)
                .build();
        populateAndAssert(populateConfig);
    }

    private void populateAndAssert(PopulateConfig populateConfig) {
        DiverseJavaTypes result = new PopulateFactory(populateConfig).populate(DiverseJavaTypes.class);

        assertThat(result).isNotNull();
        assertThat(result).hasNoNullFieldsOrProperties();
        assertThat(result.getThrowable().getMessage()).isNotNull();
        assertThat(result.getException().getMessage()).isNotNull();
        assertThat(result.getRuntimeException().getMessage()).isNotNull();
        assertThat(result.getError().getMessage()).isNotNull();
        assertThat(result.getPriorityQueue()).isNotNull().isNotEmpty();
        assertThat(result.getScanner()).isNotNull().hasNext();
        assertThat(result.getStream().count()).isPositive();
        assertThat(result.getIntStream().count()).isPositive();
        assertThat(result.getLongStream().count()).isPositive();
        assertThat(result.getDoubleStream().count()).isPositive();
        assertThat(result.getIterator().hasNext()).isTrue();
        assertThat(result.getIterable()).isNotNull().isNotEmpty();
        assertThat(result.getCompletableFuture()).isDone();
        assertThat(result.getFuture()).isDone();
        assertThat(result.getStreamInteger().count()).isPositive();
        assertThat(result.getPriorityQueueInteger()).isNotNull().isNotEmpty();
        assertThat(result.getIteratorBoolean().hasNext()).isTrue();
        assertThat(result.getIterableLong()).isNotNull().isNotEmpty();
        assertThat(result.getCompletableFutureInteger()).isDone();
        assertThat(result.getFutureBoolean()).isDone();

        if (populateConfig.isObjectFactoryEnabled()) {
            assertGeneratedCode(result, populateConfig);
        }
    }
}
