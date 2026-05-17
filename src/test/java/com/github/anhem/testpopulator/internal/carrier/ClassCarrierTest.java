package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.*;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getLargestConstructor;
import static org.assertj.core.api.Assertions.assertThat;

class ClassCarrierTest {

    private static final PopulateConfig CONFIG_NULL_ON_CIRCULAR = PopulateConfig.builder()
            .nullOnCircularDependency(true)
            .build();
    private static final PopulateConfig CONFIG_NO_NULL_ON_CIRCULAR = PopulateConfig.builder()
            .nullOnCircularDependency(false)
            .build();

    @Test
    void isCollectionCarrierReturnsFalseForClassCarrier() {
        assertThat(createClassCarrier() instanceof CollectionCarrier).isFalse();
    }

    @Test
    void hasConstructorsReturnsTrue() {
        assertThat(new CollectionCarrier<>(HashMap.class, getArbitraryParameter(), new ObjectFactoryVoid(), new ArrayList<>(), CONFIG_NULL_ON_CIRCULAR).hasConstructors()).isTrue();
    }

    @Test
    void hasConstructorsReturnsFalse() {
        assertThat(new CollectionCarrier<>(Map.class, getArbitraryParameter(), new ObjectFactoryVoid(), new ArrayList<>(), CONFIG_NULL_ON_CIRCULAR).hasConstructors()).isFalse();
    }

    @Test
    void alreadyVisitedReturnsTrueWhenClassHasBeenVisited() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid(), CONFIG_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.toClassCarrier(A.class);

        assertThat(classCarrier.alreadyVisited()).isTrue();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenNullOnCircularDependencyIsFalse() {
        ClassCarrier<A> classCarrier = ClassCarrier.initialize(A.class, new ObjectFactoryVoid(), CONFIG_NO_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.toClassCarrier(A.class);

        assertThat(classCarrier.alreadyVisited()).isFalse();
    }

    @Test
    void alreadyVisitedReturnsFalseWhenBaseJavaClass() {
        ClassCarrier<String> classCarrier = ClassCarrier.initialize(String.class, new ObjectFactoryVoid(), CONFIG_NULL_ON_CIRCULAR);

        assertThat(classCarrier.alreadyVisited()).isFalse();

        classCarrier = classCarrier.toClassCarrier(String.class);

        assertThat(classCarrier.alreadyVisited()).isFalse();
    }

    private static ClassCarrier<String> createClassCarrier() {
        return Carrier.initialize(String.class, new ObjectFactoryVoid(), CONFIG_NULL_ON_CIRCULAR);
    }

    private static Parameter getArbitraryParameter() {
        return Arrays.stream(getLargestConstructor(AllArgsConstructor.class, false).getParameters())
                .filter(p -> p.getType().equals(Set.class))
                .findFirst()
                .orElseThrow();
    }
}
