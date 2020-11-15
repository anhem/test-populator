package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.lombok.LombokImmutableWithSingular;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.LombokUtil.getDeclaredMethodsGroupedByInvokeOrder;
import static org.assertj.core.api.Assertions.assertThat;

class LombokUtilTest {

    @Test
    public void lombokMethodInvokeOrderReturnsOrderNumberForMethod() {
        Map<Integer, List<Method>> methodsGroupedByInvokeOrder = getDeclaredMethodsGroupedByInvokeOrder(LombokImmutableWithSingular.LombokImmutableWithSingularBuilder.class);

        Map<Integer, List<String>> methodNamesGroupedByInvokeOrder = methodsGroupedByInvokeOrder.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(Method::getName)
                        .collect(Collectors.toList())));

        assertThat(methodNamesGroupedByInvokeOrder.get(0)).contains("build");
        assertThat(methodNamesGroupedByInvokeOrder.get(1)).contains("stringValue");
        assertThat(methodNamesGroupedByInvokeOrder.get(1)).contains("setOfString");
        assertThat(methodNamesGroupedByInvokeOrder.get(2)).contains("clearSetOfStrings");
        assertThat(methodNamesGroupedByInvokeOrder.get(3)).contains("setOfStrings");
    }

}
