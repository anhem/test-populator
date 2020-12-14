package com.github.anhem.testpopulator.config;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.FIELD;

/**
 * Configuration for PopulateFactory
 */
public class PopulateConfig {

    private static final List<Strategy> defaultStrategyOrder = Arrays.asList(CONSTRUCTOR, FIELD);
    private static final boolean defaultRandomValues = true;
    private static final String defaultSetterPrefix = "set";

    /**
     * Builder for PopulateConfig
     */
    public static class PopulateConfigBuilder {

        private List<Strategy> strategyOrder = new ArrayList<>();
        private List<OverridePopulate<?>> overridePopulate = new ArrayList<>();
        private Boolean randomValues;
        private BuilderPattern builderPattern;
        private String setterPrefix;

        public PopulateConfigBuilder strategyOrder(List<Strategy> strategyOrder) {
            this.strategyOrder = strategyOrder;
            return this;
        }

        public PopulateConfigBuilder strategyOrder(Strategy strategy) {
            strategyOrder.add(strategy);
            return this;
        }

        public PopulateConfigBuilder overridePopulate(List<OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = overridePopulates;
            return this;
        }

        public PopulateConfigBuilder overridePopulate(OverridePopulate<?> overridePopulate) {
            this.overridePopulate.add(overridePopulate);
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

        public PopulateConfigBuilder setterPrefix(String setterPrefix) {
            this.setterPrefix = setterPrefix;
            return this;
        }

        public PopulateConfig build() {
            return new PopulateConfig(strategyOrder, overridePopulate, randomValues, builderPattern, setterPrefix);
        }
    }

    public static PopulateConfigBuilder builder() {
        return new PopulateConfigBuilder();
    }

    private final List<Strategy> strategyOrder;
    private final List<OverridePopulate<?>> overridePopulate;
    private final boolean randomValues;
    private final BuilderPattern builderPattern;
    private final String setterPrefix;

    private PopulateConfig(List<Strategy> strategyOrder,
                           List<OverridePopulate<?>> overridePopulate,
                           Boolean randomValues,
                           BuilderPattern builderPattern,
                           String setterPrefix) {
        this.strategyOrder = strategyOrder.isEmpty() ? defaultStrategyOrder : strategyOrder;
        this.overridePopulate = overridePopulate;
        this.randomValues = randomValues == null ? defaultRandomValues : randomValues;
        this.builderPattern = builderPattern;
        this.setterPrefix = setterPrefix == null ? defaultSetterPrefix : setterPrefix;
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

    public String getSetterPrefix() {
        return setterPrefix;
    }
}
