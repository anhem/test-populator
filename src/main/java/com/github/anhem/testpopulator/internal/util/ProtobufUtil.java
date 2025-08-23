package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.PopulateConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.BuilderPattern.PROTOBUF;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getDeclaredMethods;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getMethodFormat;

public class ProtobufUtil {

    private static final String BUILDER_PARAM_SUFFIX = "Builder";
    private static final String ADD_ALL_METHOD_PREFIX = "addAll";
    private static final String SET_METHOD_FORMAT = getMethodFormat("set");
    private static final String ADD_METHOD_FORMAT = getMethodFormat("add");
    private static final String PUT_METHOD_FORMAT = getMethodFormat("put");
    private static final String SET_BYTES_METHOD_SUFFIX = "Bytes";
    private static final String SET_BYTES_METHOD_REGEX = "^(set|add)[A-Z][a-zA-Z0-9]*" + SET_BYTES_METHOD_SUFFIX + "$";
    private static final String SET_ENUM_VALUE_METHOD_SUFFIX = "Value";
    private static final String SET_ENUM_VALUE_METHOD_REGEX = "^(set)[A-Z][a-zA-Z0-9]*" + SET_ENUM_VALUE_METHOD_SUFFIX + "$";
    private static final String SET_UNKNOWN_FIELDS_METHOD_NAME = "setUnknownFields";
    private static final String UNKNOWN_FIELD_SET_PARAM = "com.google.protobuf.UnknownFieldSet";
    private static final String BYTE_STRING_NAME = "com.google.protobuf.ByteString";

    private ProtobufUtil() {
    }

    public static boolean isProtobufByteString(Parameter parameter, PopulateConfig populateConfig) {
        return populateConfig.getBuilderPattern().equals(PROTOBUF) && parameter.getType().getName().equals(BYTE_STRING_NAME);
    }

    public static <T> boolean isProtobufByteString(Class<T> clazz, PopulateConfig populateConfig) {
        return populateConfig.getBuilderPattern().equals(PROTOBUF) && clazz.getName().equals(BYTE_STRING_NAME);
    }

    public static <T> List<Method> getMethodsForProtobufBuilder(Class<T> clazz, List<String> blacklistedMethods) {
        List<Method> filteredMethods = getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(ProtobufUtil::isPublic)
                .filter(method -> isChainable(method, clazz))
                .filter(PopulateUtil::hasAtLeastOneParameter)
                .filter(ProtobufUtil::isValidMutator)
                .filter(method -> !hasBuilderParameter(method))
                .filter(method -> !isSetUnknownFields(method))
                .collect(Collectors.toList());
        Set<String> methodNames = filteredMethods.stream()
                .map(Method::getName)
                .collect(Collectors.toSet());
        return filteredMethods.stream()
                .filter(method -> !isSetBytesMethod(method, methodNames))
                .filter(method -> !isUnsafeEnumSetter(method, methodNames))
                .collect(Collectors.toList());
    }

    private static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    private static boolean isChainable(Method method, Class<?> clazz) {
        return method.getReturnType().equals(clazz);
    }

    private static boolean isValidMutator(Method method) {
        boolean isAdder = method.getName().matches(ADD_METHOD_FORMAT);
        if (isAdder && isAddAllMethod(method)) {
            return false;
        }
        boolean isSetter = method.getName().matches(SET_METHOD_FORMAT);
        boolean isPutter = method.getName().matches(PUT_METHOD_FORMAT);
        return hasValidParameterCount(method, isAdder, isSetter, isPutter);

    }

    private static boolean isAddAllMethod(Method method) {
        return method.getName().startsWith(ADD_ALL_METHOD_PREFIX)
                && method.getParameterCount() == 1
                && Iterable.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    private static boolean hasValidParameterCount(Method method, boolean isAdder, boolean isSetter, boolean isPutter) {
        if (isPutter) {
            return method.getParameterCount() == 2;
        }
        if (isSetter || isAdder) {
            return method.getParameterCount() == 1;
        }
        return false;
    }

    private static boolean hasBuilderParameter(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .anyMatch(param -> param.getName().endsWith(BUILDER_PARAM_SUFFIX));
    }

    private static boolean isSetUnknownFields(Method method) {
        return method.getName().equals(SET_UNKNOWN_FIELDS_METHOD_NAME)
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0].getName().equals(UNKNOWN_FIELD_SET_PARAM);
    }

    private static boolean isSetBytesMethod(Method method, Set<String> methodNames) {
        String methodName = method.getName();
        if (methodName.matches(SET_BYTES_METHOD_REGEX)) {
            String regularMethodName = methodName.substring(0, methodName.length() - SET_BYTES_METHOD_SUFFIX.length());
            return methodNames.contains(regularMethodName);
        }
        return false;
    }

    private static boolean isUnsafeEnumSetter(Method method, Set<String> methodNames) {
        String methodName = method.getName();
        if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == int.class && methodName.matches(SET_ENUM_VALUE_METHOD_REGEX)) {
            String regularMethodName = methodName.substring(0, methodName.length() - SET_ENUM_VALUE_METHOD_SUFFIX.length());
            return methodNames.contains(regularMethodName);
        }
        return false;
    }
}