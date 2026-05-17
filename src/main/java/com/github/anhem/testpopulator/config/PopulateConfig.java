package com.github.anhem.testpopulator.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    public static final Set<String> DEFAULT_BLACKLISTED_METHODS = Set.of("$jacocoInit");
    public static final Set<String> DEFAULT_BLACKLISTED_FIELDS = Set.of("__$lineHits$__", "$jacocoData");
    public static final List<Strategy> DEFAULT_STRATEGY_ORDER = List.of(CONSTRUCTOR, SETTER, STATIC_METHOD);
    public static final boolean DEFAULT_RANDOM_VALUES = true;
    public static final boolean DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS = false;
    public static final Set<String> DEFAULT_SETTER_PREFIXES = Set.of("set");
    public static final boolean DEFAULT_OBJECT_FACTORY_ENABLED = false;
    public static final boolean DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY = false;
    public static final ConstructorType DEFAULT_CONSTRUCTOR_TYPE = NO_ARGS;
    public static final BuilderPattern DEFAULT_BUILDER_PATTERN = CUSTOM;
    public static final String DEFAULT_BUILDER_METHOD = "builder";
    public static final String PROTOBUF_BUILDER_METHOD = "newBuilder";
    public static final String DEFAULT_BUILD_METHOD = "build";
    public static final MethodType DEFAULT_METHOD_TYPE = MethodType.LARGEST;
    public static final boolean DEFAULT_KOTLIN_SUPPORT = false;
    public static final boolean DEFAULT_USE_KOTLIN_DEFAULT_VALUES = false;

    public static class PopulateConfigBuilder {
        private Set<String> blacklistedMethods = null;
        private Set<String> blacklistedFields = null;
        private List<Strategy> strategyOrder = null;
        private Map<Class<?>, OverridePopulate<?>> classOverrides = new HashMap<>();
        private Map<OverrideTarget, OverridePopulate<?>> nameOverrides = new HashMap<>();
        private BuilderPattern builderPattern;
        private Boolean randomValues;
        private Boolean accessNonPublicConstructors;
        private Set<String> setterPrefixes = null;
        private Boolean objectFactoryEnabled;
        private Boolean nullOnCircularDependency;
        private ConstructorType constructorType;
        private String builderMethod;
        private String buildMethod;
        private String objectFactoryPath;
        private MethodType methodType;
        private Boolean kotlinSupport;
        private Boolean useKotlinDefaultValues;

        /**
         * Set blacklisted methods, replacing existing ones.
         *
         * @param blacklistedMethods name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setBlacklistedMethods(Collection<String> blacklistedMethods) {
            this.blacklistedMethods = new HashSet<>(blacklistedMethods);
            return this;
        }

        /**
         * Set blacklisted methods, replacing existing ones.
         *
         * @param blacklistedMethods name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setBlacklistedMethods(String... blacklistedMethods) {
            return setBlacklistedMethods(Arrays.asList(blacklistedMethods));
        }

        /**
         * Add blacklisted methods to existing ones.
         *
         * @param blacklistedMethods name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedMethods(Collection<String> blacklistedMethods) {
            if (this.blacklistedMethods == null) {
                this.blacklistedMethods = new HashSet<>();
            }
            this.blacklistedMethods.addAll(blacklistedMethods);
            return this;
        }

        /**
         * Add blacklisted methods to existing ones.
         *
         * @param blacklistedMethods name of methods to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedMethods(String... blacklistedMethods) {
            return addBlacklistedMethods(Arrays.asList(blacklistedMethods));
        }

        /**
         * Clear blacklisted methods.
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearBlacklistedMethods() {
            this.blacklistedMethods = new HashSet<>();
            return this;
        }

        /**
         * Set blacklist fields, replacing existing ones.
         *
         * @param blacklistedFields name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setBlacklistedFields(Collection<String> blacklistedFields) {
            this.blacklistedFields = new HashSet<>(blacklistedFields);
            return this;
        }

        /**
         * Set blacklist fields, replacing existing ones.
         *
         * @param blacklistedFields name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setBlacklistedFields(String... blacklistedFields) {
            return setBlacklistedFields(Arrays.asList(blacklistedFields));
        }

        /**
         * Add blacklist fields to existing ones.
         *
         * @param blacklistedFields name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedFields(Collection<String> blacklistedFields) {
            if (this.blacklistedFields == null) {
                this.blacklistedFields = new HashSet<>();
            }
            this.blacklistedFields.addAll(blacklistedFields);
            return this;
        }

        /**
         * Add blacklist fields to existing ones.
         *
         * @param blacklistedFields name of fields to ignore
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addBlacklistedFields(String... blacklistedFields) {
            return addBlacklistedFields(Arrays.asList(blacklistedFields));
        }

        /**
         * Clear blacklisted fields.
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearBlacklistedFields() {
            this.blacklistedFields = new HashSet<>();
            return this;
        }

        /**
         * Reorder existing strategy order. This overwrites any already existing strategy orders.
         *
         * @param strategyOrder contains a list of strategies to use when populating in order of appearance
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder reorderStrategies(Strategy... strategyOrder) {
            this.strategyOrder = new ArrayList<>(Arrays.asList(strategyOrder));
            return this;
        }

        private void addStrategyOrder(Strategy strategy) {
            if (this.strategyOrder == null) {
                this.strategyOrder = new ArrayList<>();
            }
            if (!this.strategyOrder.contains(strategy)) {
                this.strategyOrder = concat(this.strategyOrder, strategy);
            }
        }

        /**
         * Set class overrides, replacing existing ones.
         *
         * @param classOverrides implementations from this map will be used whenever a class they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setClassOverrides(Map<Class<?>, OverridePopulate<?>> classOverrides) {
            this.classOverrides = new HashMap<>(classOverrides);
            return this;
        }

        /**
         * Add class overrides to existing ones.
         *
         * @param classOverrides implementations from this map will be used whenever a class they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addClassOverrides(Map<Class<?>, OverridePopulate<?>> classOverrides) {
            this.classOverrides.putAll(classOverrides);
            return this;
        }

        /**
         * Add a class override.
         *
         * @param clazz class to override
         * @param overridePopulate implementation to use
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addOverride(Class<?> clazz, OverridePopulate<?> overridePopulate) {
            this.classOverrides.put(clazz, overridePopulate);
            return this;
        }

        /**
         * Set name overrides, replacing existing ones.
         *
         * @param nameOverrides implementations from this map will be used whenever a field or method name they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setNameOverrides(Map<OverrideTarget, OverridePopulate<?>> nameOverrides) {
            this.nameOverrides = new HashMap<>(nameOverrides);
            return this;
        }

        /**
         * Add name overrides to existing ones.
         *
         * @param nameOverrides implementations from this map will be used whenever a field or method name they can produce is encountered.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addNameOverrides(Map<OverrideTarget, OverridePopulate<?>> nameOverrides) {
            this.nameOverrides.putAll(nameOverrides);
            return this;
        }

        /**
         * Add a name override.
         *
         * @param name name to override
         * @param clazz class to override
         * @param overridePopulate implementation to use
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addOverride(String name, Class<?> clazz, OverridePopulate<?> overridePopulate) {
            this.nameOverrides.put(OverrideTarget.of(name, clazz), overridePopulate);
            return this;
        }

        /**
         * Clear all class overrides.
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearClassOverrides() {
            this.classOverrides = new HashMap<>();
            return this;
        }

        /**
         * Clear all name overrides.
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearNameOverrides() {
            this.nameOverrides = new HashMap<>();
            return this;
        }

        /**
         * Clear all overrides (both class and name).
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearOverrides() {
            return clearClassOverrides().clearNameOverrides();
        }

        /**
         * Different builders behave slightly different. The builderPattern sets which one to use.
         *
         * @param builderPattern supports LOMBOK, IMMUTABLES or PROTOBUF
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
         * Set setter prefixes, replacing existing ones.
         *
         * @param setterPrefixes a collection of prefixes for methods that work in a similar way as a regular setter method.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setSetterPrefixes(Collection<String> setterPrefixes) {
            this.setterPrefixes = new HashSet<>(setterPrefixes);
            return this;
        }

        /**
         * Set setter prefixes, replacing existing ones.
         *
         * @param setterPrefixes prefixes for methods that work in a similar way as a regular setter method.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder setSetterPrefixes(String... setterPrefixes) {
            return setSetterPrefixes(Arrays.asList(setterPrefixes));
        }

        /**
         * Add setter prefixes to existing ones.
         *
         * @param setterPrefixes a collection of prefixes for methods that work in a similar way as a regular setter method.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addSetterPrefixes(Collection<String> setterPrefixes) {
            if (this.setterPrefixes == null) {
                this.setterPrefixes = new HashSet<>();
            }
            this.setterPrefixes.addAll(setterPrefixes);
            return this;
        }

        /**
         * Add setter prefixes to existing ones.
         *
         * @param setterPrefixes prefixes for methods that work in a similar way as a regular setter method.
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder addSetterPrefixes(String... setterPrefixes) {
            return addSetterPrefixes(Arrays.asList(setterPrefixes));
        }

        /**
         * Clear setter prefixes.
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearSetterPrefixes() {
            this.setterPrefixes = new HashSet<>();
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
         * SMALLEST will attempt to pick a constructor with at least one parameter and fall back on NO_ARGS if none is found.
         * @param constructorType NO_ARGS, SMALLEST, LARGEST
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder constructorType(ConstructorType constructorType) {
            this.constructorType = constructorType;
            return this;
        }

        /**
         *  Set the name of the builder method used when creating objects using BUILDER strategy.
         *  This option will be ignored for LOMBOK, IMMUTABLES and PROTOBUF.
         * @param builderMethod a string representation of the method name
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder builderMethod(String builderMethod) {
            this.builderMethod = builderMethod;
            return this;
        }

        /**
         *  Set the name of the build method used when creating objects using BUILDER strategy.
         *  This option will be ignored for LOMBOK, IMMUTABLES and PROTOBUF.
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
         * Enable support for Kotlin classes. This allows populating Kotlin classes with default values.
         *
         * @param kotlinSupport true/false
         * @return configuration for Kotlin support
         */
        public KotlinSupport kotlinSupport(boolean kotlinSupport) {
            this.kotlinSupport = kotlinSupport;
            return new KotlinSupport(this);
        }

        /**
         * Clear existing strategy order. This is useful when modifying an existing configuration via toBuilder().
         *
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder clearStrategies() {
            this.strategyOrder = new ArrayList<>();
            return this;
        }

        /**
         * Configure BUILDER strategy options
         * @return configuration for BUILDER strategy
         */
        public BuilderConfig builderStrategy() {
            addStrategyOrder(BUILDER);
            return new BuilderConfig(this);
        }

        /**
         * Configure CONSTRUCTOR strategy options
         * @return configuration for CONSTRUCTOR strategy
         */
        public ConstructorConfig constructorStrategy() {
            addStrategyOrder(CONSTRUCTOR);
            return new ConstructorConfig(this);
        }

        /**
         * Configure FIELD strategy options
         * @return configuration for FIELD strategy
         */
        public FieldConfig fieldStrategy() {
            addStrategyOrder(FIELD);
            return new FieldConfig(this);
        }

        /**
         * Configure SETTER strategy options
         * @return configuration for SETTER strategy
         */
        public SetterConfig setterStrategy() {
            addStrategyOrder(SETTER);
            return new SetterConfig(this);
        }

        /**
         * Configure MUTATOR strategy options
         * @return configuration for MUTATOR strategy
         */
        public MutatorConfig mutatorStrategy() {
            addStrategyOrder(MUTATOR);
            return new MutatorConfig(this);
        }

        /**
         * Configure STATIC_METHOD strategy options
         * @return configuration for STATIC_METHOD strategy
         */
        public StaticMethodConfig staticMethodStrategy() {
            addStrategyOrder(STATIC_METHOD);
            return new StaticMethodConfig(this);
        }

        /**
         * Set the path where the ObjectFactory should write generated Java source files.
         *
         * @param objectFactoryPath path to write generated files to
         * @return PopulateConfigBuilder
         */
        public PopulateConfigBuilder objectFactoryPath(String objectFactoryPath) {
            this.objectFactoryPath = objectFactoryPath;
            return this;
        }

        /**
         * Configure Object factory options
         *
         * @param enabled true/false
         * @return configuration for Object factory
         */
        public ObjectFactoryConfig objectFactory(boolean enabled) {
            this.objectFactoryEnabled = enabled;
            return new ObjectFactoryConfig(this);
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

    private static Path resolveDefaultObjectFactoryPath() {
        if (Files.exists(Paths.get("build.gradle")) || Files.exists(Paths.get("build.gradle.kts"))) {
            return Paths.get("build/generated-test-sources/test-populator");
        }
        return Paths.get("target/generated-test-sources/test-populator");
    }

    private final Set<String> blacklistedMethods;
    private final Set<String> blacklistedFields;
    private final List<Strategy> strategyOrder;
    private final Map<Class<?>, OverridePopulate<?>> classOverrides;
    private final Map<OverrideTarget, OverridePopulate<?>> nameOverrides;
    private final BuilderPattern builderPattern;
    private final boolean randomValues;
    private final boolean accessNonPublicConstructors;
    private final Set<String> setterPrefixes;
    private final boolean objectFactoryEnabled;
    private final boolean nullOnCircularDependency;
    private final ConstructorType constructorType;
    private final String builderMethod;
    private final String buildMethod;
    private final String objectFactoryPath;
    private final MethodType methodType;
    private final boolean kotlinSupport;
    private final boolean useKotlinDefaultValues;

    private PopulateConfig(PopulateConfigBuilder populateConfigBuilder) {
        this.blacklistedMethods = collectionOrDefault(populateConfigBuilder.blacklistedMethods, DEFAULT_BLACKLISTED_METHODS);
        this.blacklistedFields = collectionOrDefault(populateConfigBuilder.blacklistedFields, DEFAULT_BLACKLISTED_FIELDS);
        this.strategyOrder = collectionOrDefault(populateConfigBuilder.strategyOrder, DEFAULT_STRATEGY_ORDER);
        this.classOverrides = populateConfigBuilder.classOverrides;
        this.nameOverrides = populateConfigBuilder.nameOverrides;
        this.builderPattern = valueOrDefault(populateConfigBuilder.builderPattern, DEFAULT_BUILDER_PATTERN);
        this.randomValues = valueOrDefault(populateConfigBuilder.randomValues, DEFAULT_RANDOM_VALUES);
        this.accessNonPublicConstructors = valueOrDefault(populateConfigBuilder.accessNonPublicConstructors, DEFAULT_ACCESS_NON_PUBLIC_CONSTRUCTORS);
        this.setterPrefixes = collectionOrDefault(populateConfigBuilder.setterPrefixes, DEFAULT_SETTER_PREFIXES);
        this.objectFactoryEnabled = valueOrDefault(populateConfigBuilder.objectFactoryEnabled, DEFAULT_OBJECT_FACTORY_ENABLED);
        this.nullOnCircularDependency = valueOrDefault(populateConfigBuilder.nullOnCircularDependency, DEFAULT_NULL_ON_CIRCULAR_DEPENDENCY);
        this.constructorType = valueOrDefault(populateConfigBuilder.constructorType, DEFAULT_CONSTRUCTOR_TYPE);
        this.builderMethod = valueOrDefault(populateConfigBuilder.builderMethod, getDefaultBuilderMethod(this.builderPattern));
        this.buildMethod = valueOrDefault(populateConfigBuilder.buildMethod, DEFAULT_BUILD_METHOD);
        this.objectFactoryPath = this.objectFactoryEnabled ?
                valueOrDefault(populateConfigBuilder.objectFactoryPath, resolveDefaultObjectFactoryPath().toString()) :
                null;
        this.methodType = valueOrDefault(populateConfigBuilder.methodType, DEFAULT_METHOD_TYPE);
        this.kotlinSupport = valueOrDefault(populateConfigBuilder.kotlinSupport, DEFAULT_KOTLIN_SUPPORT);
        this.useKotlinDefaultValues = valueOrDefault(populateConfigBuilder.useKotlinDefaultValues, DEFAULT_USE_KOTLIN_DEFAULT_VALUES);
    }

    public Set<String> getBlacklistedMethods() {
        return blacklistedMethods;
    }

    public Set<String> getBlacklistedFields() {
        return blacklistedFields;
    }

    public List<Strategy> getStrategyOrder() {
        return strategyOrder;
    }

    public Map<Class<?>, OverridePopulate<?>> getClassOverrides() {
        return classOverrides;
    }

    public Map<OverrideTarget, OverridePopulate<?>> getNameOverrides() {
        return nameOverrides;
    }

    public BuilderPattern getBuilderPattern() {
        return builderPattern;
    }

    public boolean isRandomValues() {
        return randomValues;
    }

    public boolean isAccessNonPublicConstructors() {
        return accessNonPublicConstructors;
    }

    public Set<String> getSetterPrefixes() {
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

    public String getObjectFactoryPath() {
        return objectFactoryPath;
    }

    public boolean isKotlinSupport() {
        return kotlinSupport;
    }

    public boolean isUseKotlinDefaultValues() {
        return useKotlinDefaultValues;
    }

    /**
     * Convert PopulateConfig back to a builder
     *
     * @return PopulateConfigBuilder
     */
    public PopulateConfigBuilder toBuilder() {
        PopulateConfigBuilder populateConfigBuilder = PopulateConfig.builder()
                .setBlacklistedMethods(new ArrayList<>(blacklistedMethods))
                .setBlacklistedFields(new ArrayList<>(blacklistedFields))
                .setClassOverrides(new HashMap<>(classOverrides))
                .setNameOverrides(new HashMap<>(nameOverrides))
                .builderPattern(builderPattern)
                .randomValues(randomValues)
                .accessNonPublicConstructors(accessNonPublicConstructors)
                .setSetterPrefixes(new ArrayList<>(setterPrefixes))
                .objectFactoryEnabled(objectFactoryEnabled)
                .nullOnCircularDependency(nullOnCircularDependency)
                .constructorType(constructorType)
                .builderMethod(builderMethod)
                .buildMethod(buildMethod)
                .objectFactoryPath(objectFactoryPath)
                .methodType(methodType);
        populateConfigBuilder.kotlinSupport = kotlinSupport;
        populateConfigBuilder.useKotlinDefaultValues = useKotlinDefaultValues;
        populateConfigBuilder.strategyOrder = new ArrayList<>(strategyOrder);
        return populateConfigBuilder;
    }

    private void validate() {
        if (accessNonPublicConstructors && objectFactoryEnabled) {
            throw new IllegalArgumentException(INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY);
        }
        if (strategyOrder.contains(FIELD) && objectFactoryEnabled) {
            throw new IllegalArgumentException(INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY);
        }
    }

    private static <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    private static <T extends Collection<?>> T collectionOrDefault(T collection, T defaultCollection) {
        return collection == null ? defaultCollection : collection;
    }

    private String getDefaultBuilderMethod(BuilderPattern builderPattern) {
        return builderPattern == BuilderPattern.PROTOBUF ? PROTOBUF_BUILDER_METHOD : DEFAULT_BUILDER_METHOD;
    }

    @Override
    public String toString() {
        return "PopulateConfig{" +
                "blacklistedMethods=" + blacklistedMethods +
                ", blacklistedFields=" + blacklistedFields +
                ", strategyOrder=" + strategyOrder +
                ", classOverrides=" + classOverrides +
                ", nameOverrides=" + nameOverrides +
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
                ", kotlinSupport=" + kotlinSupport +
                ", useKotlinDefaultValues=" + useKotlinDefaultValues +
                '}';
    }

    /**
     * Base for nested configuration blocks
     */
    public abstract static class SubBuilder {
        protected final PopulateConfigBuilder parent;

        SubBuilder(PopulateConfigBuilder parent) {
            this.parent = parent;
        }

        /**
         * Return to the main builder to continue configuration
         * @return main builder
         */
        public PopulateConfigBuilder and() {
            return parent;
        }

        /**
         * Build and validate the configuration
         * @return built PopulateConfig
         */
        public PopulateConfig build() {
            return parent.build();
        }
    }

    /**
     * Configuration for BUILDER strategy
     */
    public static class BuilderConfig extends SubBuilder {

        BuilderConfig(PopulateConfigBuilder parent) {
            super(parent);
        }

        public BuilderConfig pattern(BuilderPattern pattern) {
            parent.builderPattern(pattern);
            return this;
        }

        public BuilderConfig builderMethod(String builderMethod) {
            parent.builderMethod(builderMethod);
            return this;
        }

        public BuilderConfig buildMethod(String buildMethod) {
            parent.buildMethod(buildMethod);
            return this;
        }
    }

    /**
     * Configuration for CONSTRUCTOR strategy
     */
    public static class ConstructorConfig extends SubBuilder {

        ConstructorConfig(PopulateConfigBuilder parent) {
            super(parent);
        }
    }

    /**
     * Configuration for FIELD strategy
     */
    public static class FieldConfig extends SubBuilder {

        FieldConfig(PopulateConfigBuilder parent) {
            super(parent);
        }
    }

    /**
     * Configuration for SETTER strategy
     */
    public static class SetterConfig extends SubBuilder {

        SetterConfig(PopulateConfigBuilder parent) {
            super(parent);
        }

        /**
         * Set setter prefixes, replacing existing ones.
         *
         * @param prefixes a collection of prefixes for methods that work in a similar way as a regular setter method.
         * @return SetterConfig
         */
        public SetterConfig setPrefixes(Collection<String> prefixes) {
            parent.setSetterPrefixes(prefixes);
            return this;
        }

        /**
         * Set setter prefixes, replacing existing ones.
         *
         * @param prefixes prefixes for methods that work in a similar way as a regular setter method.
         * @return SetterConfig
         */
        public SetterConfig setPrefixes(String... prefixes) {
            parent.setSetterPrefixes(prefixes);
            return this;
        }

        /**
         * Add setter prefixes to existing ones.
         *
         * @param prefixes a collection of prefixes for methods that work in a similar way as a regular setter method.
         * @return SetterConfig
         */
        public SetterConfig addPrefixes(Collection<String> prefixes) {
            parent.addSetterPrefixes(prefixes);
            return this;
        }

        /**
         * Add setter prefixes to existing ones.
         *
         * @param prefixes prefixes for methods that work in a similar way as a regular setter method.
         * @return SetterConfig
         */
        public SetterConfig addPrefixes(String... prefixes) {
            parent.addSetterPrefixes(prefixes);
            return this;
        }

        /**
         * Clear setter prefixes.
         *
         * @return SetterConfig
         */
        public SetterConfig clearPrefixes() {
            parent.clearSetterPrefixes();
            return this;
        }
    }

    /**
     * Configuration for MUTATOR strategy
     */
    public static class MutatorConfig extends SubBuilder {

        MutatorConfig(PopulateConfigBuilder parent) {
            super(parent);
        }

        public MutatorConfig constructorType(ConstructorType type) {
            parent.constructorType(type);
            return this;
        }
    }

    /**
     * Configuration for STATIC_METHOD strategy
     */
    public static class StaticMethodConfig extends SubBuilder {

        StaticMethodConfig(PopulateConfigBuilder parent) {
            super(parent);
        }

        public StaticMethodConfig methodType(MethodType type) {
            parent.methodType(type);
            return this;
        }
    }

    /**
     * Configuration for Kotlin support
     */
    public static class KotlinSupport extends SubBuilder {

        KotlinSupport(PopulateConfigBuilder parent) {
            super(parent);
        }

        /**
         * Use Kotlin default values when populating
         *
         * @param defaultValues true/false
         * @return KotlinSupport
         */
        public KotlinSupport defaultValues(boolean defaultValues) {
            parent.useKotlinDefaultValues = defaultValues;
            return this;
        }
    }

    /**
     * Configuration for Object factory
     */
    public static class ObjectFactoryConfig extends SubBuilder {

        ObjectFactoryConfig(PopulateConfigBuilder parent) {
            super(parent);
        }

        /**
         * Set the path where the ObjectFactory should write generated Java source files.
         *
         * @param path path to write generated files to
         * @return ObjectFactoryConfig
         */
        public ObjectFactoryConfig path(String path) {
            parent.objectFactoryPath(path);
            return this;
        }
    }
}
