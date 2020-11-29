package com.github.anhem.testpopulator.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.FIELD;

public class PopulateConfig {

    private static final List<Strategy> defaultStrategyOrder = Arrays.asList(CONSTRUCTOR, FIELD);
    private static final List<OverridePopulate<?>> defaultOverridePopulate = Collections.emptyList();
    private static final boolean defaultRandomValues = true;

    public static class PopulateConfigBuilder {

        private List<Strategy> strategyOrder;
        private List<OverridePopulate<?>> overridePopulate;
        private Boolean randomValues;
        private BuilderPattern builderPattern;

        public PopulateConfigBuilder strategyOrder(List<Strategy> strategyOrder) {
            this.strategyOrder = strategyOrder;
            return this;
        }

        public PopulateConfigBuilder overridePopulate(List<OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = overridePopulates;
            return this;
        }

        public PopulateConfigBuilder randomValues(boolean randomValues) {
            this.randomValues = randomValues;
            return this;
        }

        public PopulateConfigBuilder builderPattern(BuilderPattern builderPattern) {
            this.builderPattern = builderPattern;
            return this;
        }

        public PopulateConfig build() {
            return new PopulateConfig(strategyOrder, overridePopulate, randomValues, builderPattern);
        }
    }

    public static PopulateConfigBuilder builder() {
        return new PopulateConfigBuilder();
    }

    private final List<Strategy> strategyOrder;
    private final List<OverridePopulate<?>> overridePopulate;
    private final boolean randomValues;
    private final BuilderPattern builderPattern;

    private PopulateConfig(List<Strategy> strategyOrder,
                           List<OverridePopulate<?>> overridePopulate,
                           Boolean randomValues,
                           BuilderPattern builderPattern
    ) {
        this.strategyOrder = strategyOrder == null ? defaultStrategyOrder : strategyOrder;
        this.overridePopulate = overridePopulate == null ? defaultOverridePopulate : overridePopulate;
        this.randomValues = randomValues == null ? defaultRandomValues : randomValues;
        this.builderPattern = builderPattern;
    }

    public List<Strategy> getStrategyOrder() {
        return strategyOrder;
    }

    public Map<? extends Class<?>, OverridePopulate<?>> getOverridePopulate() {
        if (overridePopulate == null || overridePopulate.isEmpty()) {
            return Collections.emptyMap();
        }

        return overridePopulate.stream()
                .collect(Collectors.toMap(overridePopulate -> overridePopulate.create().getClass(), Function.identity()));
    }

    public boolean useRandomValues() {
        return randomValues;
    }

    public BuilderPattern getBuilderPattern() {
        return builderPattern;
    }
}
