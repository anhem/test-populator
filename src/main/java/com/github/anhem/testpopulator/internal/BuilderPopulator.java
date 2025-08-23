package com.github.anhem.testpopulator.internal;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.anhem.testpopulator.config.BuilderPattern.*;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.internal.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.util.BuilderUtil.getMethodsForCustomBuilder;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getMethodsForImmutablesBuilder;
import static com.github.anhem.testpopulator.internal.util.LombokUtil.calculateExpectedChildren;
import static com.github.anhem.testpopulator.internal.util.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.setAccessible;
import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.getMethodsForProtobufBuilder;
import static java.lang.String.format;

public class BuilderPopulator extends MethodPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        return continuePopulateUsingBuilder(classCarrier, populator);
    }

    private <T> T continuePopulateUsingBuilder(ClassCarrier<T> classCarrier, Populator populator) {
        switch (classCarrier.getPopulateConfig().getBuilderPattern()) {
            case LOMBOK:
                return continuePopulateUsingLombokBuilder(classCarrier, populator);
            case IMMUTABLES:
                return continuePopulateUsingImmutablesBuilder(classCarrier, populator);
            case CUSTOM:
                return continuePopulateUsingCustomBuilder(classCarrier, populator);
            case PROTOBUF:
                return continuePopulateUsingProtobufBuilder(classCarrier, populator);
            default:
                throw new PopulateException("Unsupported builder pattern");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingLombokBuilder(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Object builderObject = clazz.getDeclaredMethod(populateConfig.getBuilderMethod()).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(clazz, calculateExpectedChildren(builderObjectMethodsGroupedByInvokeOrder), populateConfig.getBuilderMethod(), populateConfig.getBuildMethod());
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(populateConfig.getBuildMethod());
            setAccessible(buildMethod, builderObject);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(ClassCarrier<T> classCarrier, Populator populator) {
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Class<?> immutablesGeneratedClass = getImmutablesGeneratedClass(classCarrier.getClazz());
            Object builderObject = immutablesGeneratedClass.getDeclaredMethod(populateConfig.getBuilderMethod()).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(immutablesGeneratedClass, builderObject, populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(immutablesGeneratedClass, builderObjectMethods.size(), populateConfig.getBuilderMethod(), populateConfig.getBuildMethod());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(populateConfig.getBuildMethod());
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, classCarrier.getClazz().getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingCustomBuilder(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Object builderObject = clazz.getDeclaredMethod(populateConfig.getBuilderMethod()).invoke(null);
            List<Method> builderObjectMethods = getMethodsForCustomBuilder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(clazz, builderObjectMethods.size(), populateConfig.getBuilderMethod(), populateConfig.getBuildMethod());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(populateConfig.getBuildMethod());
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, CUSTOM)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingProtobufBuilder(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Object builderObject = clazz.getDeclaredMethod(populateConfig.getBuilderMethod()).invoke(null);
            List<Method> builderObjectMethods = getMethodsForProtobufBuilder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(clazz, builderObjectMethods.size(), populateConfig.getBuilderMethod(), populateConfig.getBuildMethod());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier, populator));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(populateConfig.getBuildMethod());
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, classCarrier.getClazz().getName(), format("%s (%s)", BUILDER, PROTOBUF)), e);
        }
    }
}
