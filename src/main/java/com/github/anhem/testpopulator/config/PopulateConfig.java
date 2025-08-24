package com.github.anhem.testpopulator.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.BuilderPattern.CUSTOM;
import static com.github.anhem.testpopulator.config.ConstructorType.NO_ARGS;
import static com.github.anhem.testpopulator.config.Strategy.*;

/**
 * Configuration for PopulateFactory. Implemented as a builder to allow easy configuration with default values for everything not configured.
 * PopulateConfig.builder().build() will result in a PopulateConfig object with default configuration.
 * Calling toBuilder() on a PopulateConfig object will convert it back to a builder, making it easy to make copies of a configuration with slightly different settings.
 */
public class PopulateConfig {
    public static final String INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY = "objectFactory can not be enabled while accessNonPublicConstructors is true";
    public static final String INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY = "objectFactory can not be enabled while strategyOrder contains FIELD";
    public static final List<String> DEFAULT_BLACKLISTED_METHODS = List.of("$jacocoInit");
    public static final List<String> DEFAULT_BLACKLISTED_FIELDS = List.of("__$lineHits$__", "$jacocoData");
    public static final List<Strategy> DEFAULT_STRATEGY_ORDER = List.of(CONSTRUCTOR, SETTER, STATIC_METHOD);
    public static final boolean DEFAULT_RANDOM_VALUES = true;
    public static final boolean DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS = false;
    public static final List<String> DEFAULT_SETTER_PREFIXES = List.of("set");
    public static final boolean DEFAULT_OBJECT_FACTORY_ENABLED = false;
    public static final boolean DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY = false;
    public static final ConstructorType DEFAULT_CONSTRUCTOR_TYPE = NO_ARGS;
    public static final BuilderPattern DEFAULT_BUILDER_PATTERN = CUSTOM;
    public static final String DEFAULT_BUILDER_METHOD = "builder";
    public static final String PROTOBUF_BUILDER_METHOD = "newBuilder";
    public static final String DEFAULT_BUILD_METHOD = "build";
    public static final MethodType DEFAULT_METHOD_TYPE = MethodType.LARGEST;

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
        private ConstructorType constructorType;
        private String builderMethod;
        private String buildMethod;
        private MethodType methodType;

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
         * Add blacklisted method. I.E a method that should be ignored if encountered. This is mostly a code coverage issue and default values should be sufficient in most cases.
         *
         * @param blacklistedMethod name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedMethod(String blacklistedMethod) {
            this.blacklistedMethods = concat(this.blacklistedMethods, blacklistedMethod);
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
         * Add blacklist field. I.E a field that should be ignored if encountered. This is mostly a code coverage issue and default values should be sufficient in most cases.
         *
         * @param blacklistedField name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedField(String blacklistedField) {
            this.blacklistedFields = concat(this.blacklistedFields, blacklistedField);
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
        @Deprecated // use addStrategyOrder()
        public PopulateConfigBuilder strategyOrder(Strategy strategy) {
            this.strategyOrder = concat(this.strategyOrder, strategy);
            return this;
        }

        /**
         * Add strategy order at the end of existing list
         *
         * @param strategy declares what strategy to use when populating
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addStrategyOrder(Strategy strategy) {
            this.strategyOrder = concat(this.strategyOrder, strategy);
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
         * Controls whether to allow access to private or protected constructors when populating.
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
         * @param setterPrefix a prefix for methods that work in a similar way as a regular setter method.
         * Use empty String to match any method that follows the setter pattern without actually being named prefix*
         * @return PopulateConfigBuilder
         */
        @Deprecated //use addSetterPrefix()
        public PopulateConfigBuilder setterPrefix(String setterPrefix) {
            this.setterPrefixes = concat(setterPrefixes, setterPrefix);
            return this;
        }

        /**
         * add a setter prefix
         *
         * @param setterPrefix a prefix for methods that work in a similar way as a regular setter method.
         * Use empty String to match any method that follows the setter pattern without actually being named prefix*
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addSetterPrefix(String setterPrefix) {
            this.setterPrefixes = concat(setterPrefixes, setterPrefix);
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

        /**
         * Set what constructor is preferred when creating objects using MUTATOR strategy.
         * SMALLEST will attempt to pick a constructor with at least one parameter and fall back on NO_ARG if none is found.
         * @param constructorType NO_ARG, SMALLEST, LARGEST
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder constructorType(ConstructorType constructorType) {
            this.constructorType = constructorType;
            return this;
        }

        /**
         *  Set the name of the builder method used when creating objects using BUILDER strategy.
         *  This option will be ignored for LOMBOK and IMMUTABLES.
         * @param builderMethod a string representation of the method name
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder builderMethod(String builderMethod) {
            this.builderMethod = builderMethod;
            return this;
        }

        /**
         *  Set the name of the build method used when creating objects using BUILDER strategy.
         *  This option will be ignored for LOMBOK and IMMUTABLES.
         * @param buildMethod a string representation of the method name
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder buildMethod(String buildMethod) {
            this.buildMethod = buildMethod;
            return this;
        }

        /**
         * Set type of method to find when using STATIC_METHOD strategy.
         * SIMPLEST will attempt to calculate a complexity score for each static method and pick the simplest.
         * This is to attempt to avoid more complex methods that for example uses Iterator, StreamReader etc.
         * Methods with primitives, Strings, Boolean etc. will be prioritized instead.
         * @param methodType LARGEST, SMALLEST, SIMPLEST
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder methodType(MethodType methodType) {
            this.methodType = methodType;
            return this;
        }

        /**
         * Build and validate a configuration
         * @return built PopulateConfig
         */
        public PopulateConfig build() {
            PopulateConfig populateConfig = new PopulateConfig(this);
            populateConfig.validate();
            return populateConfig;
        }

        private <T> List<T> concat(List<T> list, T value) {
            return Stream.concat(list.stream(), Stream.of(value)).collect(Collectors.toList());
        }

    }

    /**
     *
     * @return a configuration object used to create PopulateConfig
     */
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
    private final ConstructorType constructorType;
    private final String builderMethod;
    private final String buildMethod;
    private final MethodType methodType;

    private PopulateConfig(PopulateConfigBuilder populateConfigBuilder) {
        this.blacklistedMethods = populateConfigBuilder.blacklistedMethods.isEmpty() ? DEFAULT_BLACKLISTED_METHODS : populateConfigBuilder.blacklistedMethods;
        this.blacklistedFields = populateConfigBuilder.blacklistedFields.isEmpty() ? DEFAULT_BLACKLISTED_FIELDS : populateConfigBuilder.blacklistedFields;
        this.strategyOrder = populateConfigBuilder.strategyOrder.isEmpty() ? DEFAULT_STRATEGY_ORDER : populateConfigBuilder.strategyOrder;
        this.overridePopulate = populateConfigBuilder.overridePopulate;
        this.builderPattern = populateConfigBuilder.builderPattern == null ? DEFAULT_BUILDER_PATTERN : populateConfigBuilder.builderPattern;
        this.randomValues = populateConfigBuilder.randomValues == null ? DEFAULT_RANDOM_VALUES : populateConfigBuilder.randomValues;
        this.accessNonPublicConstructors = populateConfigBuilder.accessNonPublicConstructors == null ? DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS : populateConfigBuilder.accessNonPublicConstructors;
        this.setterPrefixes = populateConfigBuilder.setterPrefixes.isEmpty() ? DEFAULT_SETTER_PREFIXES : populateConfigBuilder.setterPrefixes;
        this.objectFactoryEnabled = populateConfigBuilder.objectFactoryEnabled == null ? DEFAULT_OBJECT_FACTORY_ENABLED : populateConfigBuilder.objectFactoryEnabled;
        this.nullOnCircularDependency = populateConfigBuilder.nullOnCircularDependency == null ? DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY : populateConfigBuilder.nullOnCircularDependency;
        this.constructorType = populateConfigBuilder.constructorType == null ? DEFAULT_CONSTRUCTOR_TYPE : populateConfigBuilder.constructorType;
        switch (this.builderPattern) {
            case PROTOBUF:
                this.builderMethod = PROTOBUF_BUILDER_METHOD;
                this.buildMethod = DEFAULT_BUILD_METHOD;
                break;
            case CUSTOM:
                this.builderMethod = populateConfigBuilder.builderMethod == null ? DEFAULT_BUILDER_METHOD : populateConfigBuilder.builderMethod;
                this.buildMethod = populateConfigBuilder.buildMethod == null ? DEFAULT_BUILD_METHOD : populateConfigBuilder.buildMethod;
                break;
            default:
                this.builderMethod = DEFAULT_BUILDER_METHOD;
                this.buildMethod = DEFAULT_BUILD_METHOD;
        }
        this.methodType = populateConfigBuilder.methodType == null ? DEFAULT_METHOD_TYPE : populateConfigBuilder.methodType;
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

    public ConstructorType getConstructorType() {
        return constructorType;
    }

    public String getBuilderMethod() {
        return builderMethod;
    }

    public String getBuildMethod() {
        return buildMethod;
    }

    public MethodType getMethodType() {
        return methodType;
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
                .overridePopulate(new HashMap<>(overridePopulate))
                .builderPattern(builderPattern)
                .randomValues(randomValues)
                .accessNonPublicConstructors(accessNonPublicConstructors)
                .setterPrefixes(new ArrayList<>(setterPrefixes))
                .objectFactoryEnabled(objectFactoryEnabled)
                .nullOnCircularDependency(nullOnCircularDependency)
                .constructorType(constructorType)
                .builderMethod(builderMethod)
                .buildMethod(buildMethod)
                .methodType(methodType);
    }

    private void validate() {
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
                ", constructorType=" + constructorType +
                ", builderMethod='" + builderMethod + '\'' +
                ", buildMethod='" + buildMethod + '\'' +
                ", methodType=" + methodType +
                '}';
    }
}
