package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.lombok.LombokImmutableWithSingular;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.internal.util.LombokUtil.calculateExpectedChildren;
import static com.github.anhem.testpopulator.internal.util.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class LombokUtilTest {


    @Test
    void lombokMethodInvokeOrderReturnsOrderNumberForMethod() {
        Map<Integer, List<Method>> methodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(LombokImmutableWithSingular.LombokImmutableWithSingularBuilder.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods());

        Map<Integer, List<String>> methodNamesGroupedByInvokeOrder = methodsGroupedByInvokeOrder.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(Method::getName)
                        .collect(Collectors.toList())));

        assertThat(methodNamesGroupedByInvokeOrder.keySet()).hasSize(4);
        assertThat(methodNamesGroupedByInvokeOrder.get(0)).contains("build");
        assertThat(methodNamesGroupedByInvokeOrder.get(1)).contains("stringValue");
        assertThat(methodNamesGroupedByInvokeOrder.get(1)).contains("setOfString");
        assertThat(methodNamesGroupedByInvokeOrder.get(2)).contains("clearSetOfStrings");
        assertThat(methodNamesGroupedByInvokeOrder.get(3)).contains("setOfStrings");
    }

    @Test
    void calculateExpectedChildrenReturnsNumberOfMethodsToCallInBuilder() {
        Map<Integer, List<Method>> methodsForLombokBuilderGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(LombokImmutableWithSingular.LombokImmutableWithSingularBuilder.class, DEFAULT_POPULATE_CONFIG.getBlacklistedMethods());

        int expectedChildren = calculateExpectedChildren(methodsForLombokBuilderGroupedByInvokeOrder);

        assertThat(expectedChildren).isEqualTo(44);
    }

}
