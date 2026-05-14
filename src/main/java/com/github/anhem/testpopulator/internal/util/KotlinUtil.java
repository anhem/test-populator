package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.MethodType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getDeclaredMethods;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.hasAtLeastOneParameter;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.hasSelfReferencingParameter;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.selectMethod;

public class KotlinUtil {

    public static final String KOTLIN_DEFAULT_CONSTRUCTOR_MARKER = "DefaultConstructorMarker";
    public static final String KOTLIN_DELEGATE_SUFFIX = "$delegate";

    private KotlinUtil() {
    }

    public static <T> boolean isKotlinConstructor(Constructor<T> constructor, boolean kotlinSupport) {
        if (!kotlinSupport) {
            return false;
        }
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].getSimpleName().equals(KOTLIN_DEFAULT_CONSTRUCTOR_MARKER);
    }

    public static boolean isKotlinDelegate(Field field, boolean kotlinSupport) {
        return kotlinSupport && field.getName().endsWith(KOTLIN_DELEGATE_SUFFIX);
    }

    public static boolean isKotlinSingleton(Class<?> clazz, boolean kotlinSupport) {
        return kotlinSupport && isKotlinSingleton(clazz);
    }

    public static <T> boolean isKotlinSingleton(Class<T> clazz) {
        try {
            Field instance = clazz.getDeclaredField("INSTANCE");
            return Modifier.isPublic(instance.getModifiers()) &&
                    Modifier.isStatic(instance.getModifiers()) &&
                    Modifier.isFinal(instance.getModifiers()) &&
                    instance.getType().equals(clazz);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static boolean hasKotlinCompanion(Class<?> clazz) {
        try {
            Field companion = clazz.getDeclaredField("Companion");
            return Modifier.isPublic(companion.getModifiers()) &&
                    Modifier.isStatic(companion.getModifiers()) &&
                    Modifier.isFinal(companion.getModifiers());
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static Object getCompanionObject(Class<?> clazz) {
        try {
            return clazz.getField("Companion").get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Method getCompanionMethod(Class<?> companionClass, Class<T> targetClass, Set<String> blacklistedMethods, MethodType methodType) {
        List<Method> methods = getDeclaredMethods(companionClass, blacklistedMethods).stream()
                .filter(method -> isMatchingFactoryMethod(method, targetClass))
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
        return selectMethod(methodType, methods);
    }

    public static <T> boolean isMatchingKotlinSingletonOrCompanion(Class<T> clazz, boolean kotlinSupport) {
        return kotlinSupport && (isKotlinSingleton(clazz) || hasKotlinCompanion(clazz));
    }

    static <T> boolean isMatchingFactoryMethod(Method method, Class<T> clazz) {
        return method.getReturnType().equals(clazz) &&
                hasAtLeastOneParameter(method) &&
                !hasSelfReferencingParameter(method, clazz);
    }

    public static String getCompanionMethodName(Method companionMethod) {
        return "Companion." + companionMethod.getName();
    }
}
