package com.github.anhem.testpopulator.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.*;

/**
 * Configuration for PopulateFactory
 */
public class PopulateConfig {

    private static final List<String> DEFAULT_BLACKLISTED_METHODS = List.of("$jacocoInit");
    private static final List<String> DEFAULT_BLACKLISTED_FIELDS = List.of("__$lineHits$__", "$jacocoData");
    private static final List<Strategy> DEFAULT_STRATEGY_ORDER = List.of(CONSTRUCTOR, SETTER, FIELD);
    private static final boolean DEFAULT_RANDOM_VALUES = true;
    private static final boolean DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS = false;
    private static final String DEFAULT_SETTER_PREFIX = "set";
    private static final boolean DEFAULT_OBJECT_FACTORY_ENABLED = false;

    /**
     * Builder for PopulateConfig
     */
    public static class PopulateConfigBuilder {

        private List<String> blacklistedMethods = new ArrayList<>();
        private List<String> blacklistedFields = new ArrayList<>();
        private List<Strategy> strategyOrder = new ArrayList<>();
        private List<OverridePopulate<?>> overridePopulate = new ArrayList<>();
        private BuilderPattern builderPattern;
        private Boolean randomValues;
        private Boolean accessNonPublicConstructors;
        private String setterPrefix;

        private Boolean objectFactoryEnabled;

        public PopulateConfigBuilder blacklistedMethods(List<String> blacklistedMethods) {
            this.blacklistedMethods = blacklistedMethods;
            return this;
        }

        public PopulateConfigBuilder blacklistedFields(List<String> blacklistedFields) {
            this.blacklistedFields = blacklistedFields;
            return this;
        }

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

        public PopulateConfigBuilder builderPattern(BuilderPattern builderPattern) {
            this.builderPattern = builderPattern;
            return this;
        }

        public PopulateConfigBuilder randomValues(boolean randomValues) {
            this.randomValues = randomValues;
            return this;
        }

        public PopulateConfigBuilder accessNonPublicConstructors(boolean accessNonPublicConstructor) {
            this.accessNonPublicConstructors = accessNonPublicConstructor;
            return this;
        }

        public PopulateConfigBuilder setterPrefix(String setterPrefix) {
            this.setterPrefix = setterPrefix;
            return this;
        }

        public PopulateConfigBuilder objectFactoryEnabled(boolean objectFactoryEnabled) {
            this.objectFactoryEnabled = objectFactoryEnabled;
            return this;
        }

        public PopulateConfig build() {
            return new PopulateConfig(
                    blacklistedMethods,
                    blacklistedFields,
                    strategyOrder,
                    overridePopulate,
                    builderPattern,
                    randomValues,
                    accessNonPublicConstructors,
                    setterPrefix,
                    objectFactoryEnabled
            );
        }
    }

    public static PopulateConfigBuilder builder() {
        return new PopulateConfigBuilder();
    }

    private final List<String> blacklistedMethods;
    private final List<String> blacklistedFields;
    private final List<Strategy> strategyOrder;
    private final List<OverridePopulate<?>> overridePopulate;
    private final BuilderPattern builderPattern;
    private final boolean randomValues;
    private final boolean accessNonPublicConstructors;
    private final String setterPrefix;

    private final boolean objectFactoryEnabled;

    private PopulateConfig(List<String> blacklistedMethods,
                           List<String> blacklistedFields,
                           List<Strategy> strategyOrder,
                           List<OverridePopulate<?>> overridePopulate,
                           BuilderPattern builderPattern,
                           Boolean randomValues,
                           Boolean accessNonPublicConstructors,
                           String setterPrefix,
                           Boolean objectFactoryEnabled) {
        this.blacklistedMethods = blacklistedMethods.isEmpty() ? DEFAULT_BLACKLISTED_METHODS : blacklistedMethods;
        this.blacklistedFields = blacklistedFields.isEmpty() ? DEFAULT_BLACKLISTED_FIELDS : blacklistedFields;
        this.strategyOrder = strategyOrder.isEmpty() ? DEFAULT_STRATEGY_ORDER : strategyOrder;
        this.overridePopulate = overridePopulate;
        this.builderPattern = builderPattern;
        this.randomValues = randomValues == null ? DEFAULT_RANDOM_VALUES : randomValues;
        this.accessNonPublicConstructors = accessNonPublicConstructors == null ? DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS : accessNonPublicConstructors;
        this.setterPrefix = setterPrefix == null ? DEFAULT_SETTER_PREFIX : setterPrefix;
        this.objectFactoryEnabled = objectFactoryEnabled == null ? DEFAULT_OBJECT_FACTORY_ENABLED : objectFactoryEnabled;
    }

    public List<String> getBlacklistedMethods() {
        return blacklistedMethods;
    }

    public List<String> getBlacklistedFields() {
        return blacklistedFields;
    }

    public List<Strategy> getStrategyOrder() {
        return strategyOrder;
    }

    public Map<Class<?>, OverridePopulate<?>> createOverridePopulates() {
        if (overridePopulate == null || overridePopulate.isEmpty()) {
            return Collections.emptyMap();
        }

        return overridePopulate.stream()
                .collect(Collectors.toMap(overridePopulate -> overridePopulate.create().getClass(), Function.identity()));
    }

    public BuilderPattern getBuilderPattern() {
        return builderPattern;
    }

    public boolean useRandomValues() {
        return randomValues;
    }

    public boolean canAccessNonPublicConstructors() {
        return accessNonPublicConstructors;
    }

    public String getSetterPrefix() {
        return setterPrefix;
    }

    public boolean isObjectFactoryEnabled() {
        return objectFactoryEnabled;
    }

    public PopulateConfigBuilder toBuilder() {
        return PopulateConfig.builder()
                .blacklistedMethods(blacklistedMethods)
                .blacklistedFields(blacklistedFields)
                .strategyOrder(strategyOrder)
                .overridePopulate(overridePopulate)
                .builderPattern(builderPattern)
                .randomValues(randomValues)
                .accessNonPublicConstructors(accessNonPublicConstructors)
                .setterPrefix(setterPrefix)
                .objectFactoryEnabled(objectFactoryEnabled);
    }
}
