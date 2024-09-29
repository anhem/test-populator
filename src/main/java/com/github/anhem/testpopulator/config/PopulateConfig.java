package com.github.anhem.testpopulator.config;

import java.util.*;

import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.lang.String.format;

/**
 * Configuration for PopulateFactory. Implemented as a builder to allow easy configuration with default values for everything not configured.
 * PopulateConfig.builder().build() will result in a PopulateConfig object with default configuration.
 * Calling toBuilder() on a PopulateConfig object will convert it back to a builder, making it easy to make copies of a configuration with slightly different settings.
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
    public static final List<String> DEFAULT_SETTER_PREFIXES = List.of("set");
    public static final boolean DEFAULT_OBJECT_FACTORY_ENABLED = false;
    public static final boolean DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY = false;

    public static class PopulateConfigBuilder {

        private List<String> blacklistedMethods = new ArrayList<>();
        private List<String> blacklistedFields = new ArrayList<>();
        private List<Strategy> strategyOrder = new ArrayList<>();
        private Map<Class<?>, OverridePopulate<?>> overridePopulate = new HashMap<>();
        private BuilderPattern builderPattern;
        private Boolean randomValues;
        private Boolean accessNonPublicConstructors;
        private List<String> setterPrefixes = new ArrayList<>();
        private Boolean objectFactoryEnabled;
        private Boolean nullOnCircularDependency;

        /**
         * Set blacklisted methods. I.E methods that should be ignored if encountered. This is mostly a code coverage issue and default values should be sufficient in most cases.
         *
         * @param blacklistedMethods name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder blacklistedMethods(List<String> blacklistedMethods) {
            this.blacklistedMethods = blacklistedMethods;
            return this;
        }

        /**
         * Set blacklist fields. I.E fields that should be ignored if encountered. This is mostly a code coverage issue and default values should be sufficient in most cases.
         *
         * @param blacklistedFields name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder blacklistedFields(List<String> blacklistedFields) {
            this.blacklistedFields = blacklistedFields;
            return this;
        }

        /**
         * Set strategy order, overwriting any already existing strategy orders.
         *
         * @param strategyOrder contains a list of strategies to use when populating in order of appearance
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder strategyOrder(List<Strategy> strategyOrder) {
            this.strategyOrder = strategyOrder;
            return this;
        }

        /**
         * Add strategy order at the end of existing list
         *
         * @param strategy declares what strategy to use when populating
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder strategyOrder(Strategy strategy) {
            strategyOrder.add(strategy);
            return this;
        }

        /**
         * Provide your own implementations of how objects should be created when those classes are encountered during population.
         * This overwrites any existing overridePopulates.
         *
         * @param overridePopulates implementations from this list will be used whenever a class they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        @Deprecated //use overridePopulate(Map<Class<?>, OverridePopulate<?>> overridePopulates)
        public PopulateConfigBuilder overridePopulate(List<OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = new HashMap<>();
            overridePopulates.forEach(this::overridePopulate);
            return this;
        }

        /**
         * Add your own implementation of how an object should be created when that class is encountered during population.
         *
         * @param overridePopulate this implementation will be used whenever a class it can produce is encountered.
         * @return PopulateConfigBuilder
         */
        @Deprecated //use overridePopulate(Class<?> clazz, OverridePopulate<?> overridePopulate)
        public PopulateConfigBuilder overridePopulate(OverridePopulate<?> overridePopulate) {
            this.overridePopulate.put(overridePopulate.create().getClass(), overridePopulate);
            return this;
        }

        /**
         * Provide your own implementations of how objects should be created when those classes are encountered during population.
         * This overwrites any existing overridePopulates.
         *
         * @param overridePopulates implementations from this list will be used whenever a class they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder overridePopulate(Map<Class<?>, OverridePopulate<?>> overridePopulates) {
            this.overridePopulate = overridePopulates;
            return this;
        }

        /**
         * Add your own implementation of how an object should be created when that class is encountered during population.
         * @param overridePopulate this implementation will be used whenever a class it can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder overridePopulate(Class<?> clazz, OverridePopulate<?> overridePopulate) {
            this.overridePopulate.put(clazz, overridePopulate);
            return this;
        }

        /**
         * Different builders behave slightly different. The builderPattern sets which one to use.
         *
         * @param builderPattern supports LOMBOK or IMMUTABLES
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder builderPattern(BuilderPattern builderPattern) {
            this.builderPattern = builderPattern;
            return this;
        }

        /**
         * Declares if random or fixed values should be used. Random values are not created entirely at random. They are created to be random enough.
         *
         * @param randomValues true/false
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder randomValues(boolean randomValues) {
            this.randomValues = randomValues;
            return this;
        }

        /**
         * Controls whether to allow access to private constructors when populating.
         *
         * @param accessNonPublicConstructor true/false
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder accessNonPublicConstructors(boolean accessNonPublicConstructor) {
            this.accessNonPublicConstructors = accessNonPublicConstructor;
            return this;
        }

        /**
         * Use setters with a different format than set*, overwriting any already existing setters.
         *
         * @param setterPrefixes a list of prefixes for methods that work in a similar way as a regular setter method.
         * Use empty String to match any method that follows the setter pattern without actually being named prefix*
         */
        public PopulateConfigBuilder setterPrefixes(List<String> setterPrefixes) {
            this.setterPrefixes = setterPrefixes;
            return this;
        }

        /**
         * Use setters with a different format than set*
         *
         * @param setterPrefix a prefixe for methods that work in a similar way as a regular setter method.
         * Use empty String to match any method that follows the setter pattern without actually being named prefix*
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setterPrefix(String setterPrefix) {
            this.setterPrefixes.add(setterPrefix);
            return this;
        }

        /**
         * This will result in populated objects to also be generated as java code in target/generated-test-sources/test-populator/.
         * These files can then be copied into your project and used as any other java class.
         *
         * @param objectFactoryEnabled true/false
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder objectFactoryEnabled(boolean objectFactoryEnabled) {
            this.objectFactoryEnabled = objectFactoryEnabled;
            return this;
        }

        /**
         * Enable to solve issues with classes having circular dependencies.
         *
         * @param nullOnCircularDependency true/false
         * @return PopulateConfigBuilder
         */
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
                    setterPrefixes,
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
    private final List<String> setterPrefixes;
    private final boolean objectFactoryEnabled;
    private final boolean nullOnCircularDependency;

    private PopulateConfig(List<String> blacklistedMethods,
                           List<String> blacklistedFields,
                           List<Strategy> strategyOrder,
                           Map<Class<?>, OverridePopulate<?>> overridePopulate,
                           BuilderPattern builderPattern,
                           Boolean randomValues,
                           Boolean accessNonPublicConstructors,
                           List<String> setterPrefixes,
                           Boolean objectFactoryEnabled,
                           Boolean nullOnCircularDependency) {
        this.blacklistedMethods = blacklistedMethods.isEmpty() ? DEFAULT_BLACKLISTED_METHODS : blacklistedMethods;
        this.blacklistedFields = blacklistedFields.isEmpty() ? DEFAULT_BLACKLISTED_FIELDS : blacklistedFields;
        this.strategyOrder = strategyOrder.isEmpty() ? DEFAULT_STRATEGY_ORDER : strategyOrder;
        this.overridePopulate = overridePopulate;
        this.builderPattern = builderPattern;
        this.randomValues = randomValues == null ? DEFAULT_RANDOM_VALUES : randomValues;
        this.accessNonPublicConstructors = accessNonPublicConstructors == null ? DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS : accessNonPublicConstructors;
        this.setterPrefixes = setterPrefixes.isEmpty() ? DEFAULT_SETTER_PREFIXES : setterPrefixes;
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

    public List<String> getSetterPrefixes() {
        return setterPrefixes;
    }

    public boolean isObjectFactoryEnabled() {
        return objectFactoryEnabled;
    }

    public boolean isNullOnCircularDependency() {
        return nullOnCircularDependency;
    }

    /**
     * Convert PopulateConfig back to a builder
     *
     * @return PopulateConfigBuilder
     */
    public PopulateConfigBuilder toBuilder() {
        return PopulateConfig.builder()
                .blacklistedMethods(new ArrayList<>(blacklistedMethods))
                .blacklistedFields(new ArrayList<>(blacklistedFields))
                .strategyOrder(new ArrayList<>(strategyOrder))
                .overridePopulate(new ArrayList<>(overridePopulate.values()))
                .builderPattern(builderPattern)
                .randomValues(randomValues)
                .accessNonPublicConstructors(accessNonPublicConstructors)
                .setterPrefixes(new ArrayList<>(setterPrefixes))
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
                ", setterPrefixes=" + setterPrefixes +
                ", objectFactoryEnabled=" + objectFactoryEnabled +
                ", nullOnCircularDependency=" + nullOnCircularDependency +
                '}';
    }
}
