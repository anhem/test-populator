package com.github.anhem.testpopulator.config;

import java.util.*;

import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.lang.String.format;

/**
 * Configuration for PopulateFactory
 */
public class PopulateConfig {
    public static final String INVALID_CONFIG_MISSING_BUILDER_PATTERN = "%s strategy configured, but no builderPattern specified. Should be one of %s";
    public static final String INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY = "objectFactory can not be enabled while accessNonPublicConstructors is true";
    public static final String INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY = "objectFactory can not be enabled while strategyOrder contains FIELD";
    public static final List<String> DEFAULT_BLACKLISTED_METHODS = List.of("$jacocoInit");
    public static final List<String> DEFAULT_BLACKLISTED_FIELDS = List.of("__$lineHits$__", "$jacocoData");
    public static final List<Strategy> DEFAULT_STRATEGY_ORDER = List.of(CONSTRUCTOR, SETTER);
    public static final boolean DEFAULT_RANDOM_VALUES = true;
    public static final boolean DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS = false;
    public static final String DEFAULT_SETTER_PREFIX = "set";
    public static final boolean DEFAULT_OBJECT_FACTORY_ENABLED = false;
    public static final boolean DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY = false;

    /**
     * Builder for PopulateConfig
     */
    public static class PopulateConfigBuilder {

        private List<String> blacklistedMethods = new ArrayList<>();
        private List<String> blacklistedFields = new ArrayList<>();
        private List<Strategy> strategyOrder = new ArrayList<>();
        private Map<Class<?>, OverridePopulate<?>> overridePopulate = new HashMap<>();
        private BuilderPattern builderPattern;
        private Boolean randomValues;
        private Boolean accessNonPublicConstructors;
        private String setterPrefix;
        private Boolean objectFactoryEnabled;
        private Boolean nullOnCircularDependency;

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

        @Deprecated //use overridePopulate(Map<Class<?>, OverridePopulate<?>> overridePopulates)
        public PopulateConfigBuilder overridePopulate(List<OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = new HashMap<>();
            overridePopulates.forEach(this::overridePopulate);
            return this;
        }

        @Deprecated //use overridePopulate(Class<?> clazz, OverridePopulate<?> overridePopulate)
        public PopulateConfigBuilder overridePopulate(OverridePopulate<?> overridePopulate) {
            this.overridePopulate.put(overridePopulate.create().getClass(), overridePopulate);
            return this;
        }

        public PopulateConfigBuilder overridePopulate(Map<Class<?>, OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = overridePopulates;
            return this;
        }

        public PopulateConfigBuilder overridePopulate(Class<?> clazz, OverridePopulate<?> overridePopulate) {
            this.overridePopulate.put(clazz, overridePopulate);
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

        public PopulateConfigBuilder nullOnCircularDependency(boolean nullOnCircularDependency) {
            this.nullOnCircularDependency = nullOnCircularDependency;
            return this;
        }

        public PopulateConfig build() {
            PopulateConfig populateConfig = new PopulateConfig(
                    blacklistedMethods,
                    blacklistedFields,
                    strategyOrder,
                    overridePopulate,
                    builderPattern,
                    randomValues,
                    accessNonPublicConstructors,
                    setterPrefix,
                    objectFactoryEnabled,
                    nullOnCircularDependency
            );
            populateConfig.validate();
            return populateConfig;
        }
    }

    public static PopulateConfigBuilder builder() {
        return new PopulateConfigBuilder();
    }

    private final List<String> blacklistedMethods;
    private final List<String> blacklistedFields;
    private final List<Strategy> strategyOrder;
    private final Map<Class<?>, OverridePopulate<?>> overridePopulate;
    private final BuilderPattern builderPattern;
    private final boolean randomValues;
    private final boolean accessNonPublicConstructors;
    private final String setterPrefix;
    private final boolean objectFactoryEnabled;
    private final boolean nullOnCircularDependency;

    private PopulateConfig(List<String> blacklistedMethods,
                           List<String> blacklistedFields,
                           List<Strategy> strategyOrder,
                           Map<Class<?>, OverridePopulate<?>> overridePopulate,
                           BuilderPattern builderPattern,
                           Boolean randomValues,
                           Boolean accessNonPublicConstructors,
                           String setterPrefix,
                           Boolean objectFactoryEnabled,
                           Boolean nullOnCircularDependency) {
        this.blacklistedMethods = blacklistedMethods.isEmpty() ? DEFAULT_BLACKLISTED_METHODS : blacklistedMethods;
        this.blacklistedFields = blacklistedFields.isEmpty() ? DEFAULT_BLACKLISTED_FIELDS : blacklistedFields;
        this.strategyOrder = strategyOrder.isEmpty() ? DEFAULT_STRATEGY_ORDER : strategyOrder;
        this.overridePopulate = overridePopulate;
        this.builderPattern = builderPattern;
        this.randomValues = randomValues == null ? DEFAULT_RANDOM_VALUES : randomValues;
        this.accessNonPublicConstructors = accessNonPublicConstructors == null ? DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS : accessNonPublicConstructors;
        this.setterPrefix = setterPrefix == null ? DEFAULT_SETTER_PREFIX : setterPrefix;
        this.objectFactoryEnabled = objectFactoryEnabled == null ? DEFAULT_OBJECT_FACTORY_ENABLED : objectFactoryEnabled;
        this.nullOnCircularDependency = nullOnCircularDependency == null ? DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY : nullOnCircularDependency;
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

    public Map<Class<?>, OverridePopulate<?>> getOverridePopulate() {
        return overridePopulate;
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

    public boolean isNullOnCircularDependency() {
        return nullOnCircularDependency;
    }

    public PopulateConfigBuilder toBuilder() {
        return PopulateConfig.builder()
                .blacklistedMethods(new ArrayList<>(blacklistedMethods))
                .blacklistedFields(new ArrayList<>(blacklistedFields))
                .strategyOrder(new ArrayList<>(strategyOrder))
                .overridePopulate(new ArrayList<>(overridePopulate.values()))
                .builderPattern(builderPattern)
                .randomValues(randomValues)
                .accessNonPublicConstructors(accessNonPublicConstructors)
                .setterPrefix(setterPrefix)
                .objectFactoryEnabled(objectFactoryEnabled)
                .nullOnCircularDependency(nullOnCircularDependency);
    }

    private void validate() {
        if (strategyOrder.contains(BUILDER) && builderPattern == null) {
            throw new IllegalArgumentException(format(INVALID_CONFIG_MISSING_BUILDER_PATTERN, BUILDER, Arrays.toString(BuilderPattern.values())));
        }
        if (accessNonPublicConstructors && objectFactoryEnabled) {
            throw new IllegalArgumentException(INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY);
        }
        if (strategyOrder.contains(FIELD) && objectFactoryEnabled) {
            throw new IllegalArgumentException(INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY);
        }
    }

    @Override
    public String toString() {
        return "PopulateConfig{" +
                "blacklistedMethods=" + blacklistedMethods +
                ", blacklistedFields=" + blacklistedFields +
                ", strategyOrder=" + strategyOrder +
                ", overridePopulate=" + overridePopulate +
                ", builderPattern=" + builderPattern +
                ", randomValues=" + randomValues +
                ", accessNonPublicConstructors=" + accessNonPublicConstructors +
                ", setterPrefix='" + setterPrefix + '\'' +
                ", objectFactoryEnabled=" + objectFactoryEnabled +
                ", nullOnCircularDependency=" + nullOnCircularDependency +
                '}';
    }
}
