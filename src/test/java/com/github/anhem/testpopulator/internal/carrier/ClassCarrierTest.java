package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.model.circular.A;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

class ClassCarrierTest {

    private static final PopulateConfig CONFIG_NULL_ON_CIRCULAR = PopulateConfig.builder().wildcardFallbackType(String.class)
            .nullOnCircularDependency(true)
            .build();
    private static final PopulateConfig CONFIG_NO_NULL_ON_CIRCULAR = PopulateConfig.builder().wildcardFallbackType(String.class)
            .nullOnCircularDependency(false)
            .build();

    @Test
    void hasConstructorsReturnsTrue() {
        assertThat(new ClassCarrier<>(HashMap.class, "test", new ObjectFactoryVoid(), new ArrayList<>(), CONFIG_NULL_ON_CIRCULAR, emptyMap(), emptyList()).hasConstructors()).isTrue();
    }

    @Test
    void hasConstructorsReturnsFalse() {
        assertThat(new ClassCarrier<>(Map.class, "test", new ObjectFactoryVoid(), new ArrayList<>(), CONFIG_NULL_ON_CIRCULAR, emptyMap(), emptyList()).hasConstructors()).isFalse();
    }

    @Test
    void alreadyVisitedReturnsTrueWhenClassHasBeenVisited() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid(), CONFIG_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.createChild(A.class);

        assertThat(classCarrier.alreadyVisited()).isTrue();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenNullOnCircularDependencyIsFalse() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid(), CONFIG_NO_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.createChild(A.class);

        assertThat(classCarrier.alreadyVisited()).isFalse();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenBaseJavaClass() {
        ClassCarrier<String> classCarrier = ClassCarrier.initialize(String.class, new ObjectFactoryVoid(), CONFIG_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.createChild(String.class);

        assertThat(classCarrier.alreadyVisited()).isFalse();
    }
}
