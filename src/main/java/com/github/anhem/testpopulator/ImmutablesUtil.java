package com.github.anhem.testpopulator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateUtil.*;

public class ImmutablesUtil {

    static final String ADD_PREFIX = "add";
    static final String ADD_ALL_PREFIX = "addAll";
    static final String PUT_PREFIX = "put";
    static final String PUT_ALL_PREFIX = "putAll";
    private static final String ADD_METHOD_PATTERN = String.format("%s%s", ADD_PREFIX, MATCH_FIRST_CHARACTER_UPPERCASE);
    private static final String ADD_ALL_METHOD_PATTERN = String.format("%s%s", ADD_ALL_PREFIX, MATCH_FIRST_CHARACTER_UPPERCASE);
    private static final String PUT_METHOD_PATTERN = String.format("%s%s", PUT_PREFIX, MATCH_FIRST_CHARACTER_UPPERCASE);
    private static final String PUT_ALL_METHOD_PATTERN = String.format("%s%s", PUT_ALL_PREFIX, MATCH_FIRST_CHARACTER_UPPERCASE);
    public static final String CLASS_PREFIX = "Immutable";
    public static final String DOT = ".";
    public static final String FAILED_TO_FIND_CLASS = "Unable to find Immutables generated class for %s";

    private ImmutablesUtil() {
    }

    static <T> List<Method> getMethodsForImmutablesBuilder(Class<T> clazz, Object builderObject, List<String> blacklistedMethods) {
        List<Method> declaredMethods = getDeclaredMethods(builderObject.getClass(), blacklistedMethods);
        return removeMethodsDoingTheSameThing(declaredMethods).stream()
                .filter(PopulateUtil::hasAtLeastOneParameter)
                .filter(method -> !isDeclaringJavaBaseClass(method))
                .filter(method -> !isSameMethodParameterAsClass(clazz, method))
                .collect(Collectors.toList());
    }

    static Class<?> getImmutablesGeneratedClass(Class<?> clazz) {
        if (clazz.isInterface() || isAbstract(clazz)) {
            try {
                String packageName = clazz.getName().substring(0, clazz.getName().lastIndexOf(DOT));
                String className = String.format("%s.%s%s", packageName, CLASS_PREFIX, clazz.getSimpleName());
                return Class.forName(className);
            } catch (Exception e) {
                throw new PopulateException(String.format(FAILED_TO_FIND_CLASS, clazz.getName()), e);
            }
        }
        return clazz;
    }

    private static List<Method> removeMethodsDoingTheSameThing(List<Method> methods) {
        List<String> methodNames = methods.stream()
                .map(Method::getName)
                .collect(Collectors.toList());

        List<Method> methodsToRemove = methods.stream()
                .filter(ImmutablesUtil::isMatchingAnyMethodPrefixes)
                .filter(method -> !methodNames.contains(getMethodNameWithoutPrefix(method)))
                .collect(Collectors.toList());

        return methods.stream()
                .filter(method -> !methodsToRemove.contains(method))
                .collect(Collectors.toList());
    }

    private static String getMethodNameWithoutPrefix(Method method) {
        return method.getName()
                .replace(ADD_ALL_PREFIX, "")
                .replace(ADD_PREFIX, "")
                .replace(PUT_ALL_PREFIX, "")
                .replace(PUT_PREFIX, "");
    }

    private static boolean isMatchingAnyMethodPrefixes(Method method) {
        return isAddMethod(method) || isAddAllMethod(method) || isPutMethod(method) || isPutAllMethod(method);
    }

    private static boolean isAddMethod(Method method) {
        return method.getName().matches(ADD_METHOD_PATTERN);
    }

    private static boolean isAddAllMethod(Method method) {
        return method.getName().matches(ADD_ALL_METHOD_PATTERN);
    }

    private static boolean isPutMethod(Method method) {
        return method.getName().matches(PUT_METHOD_PATTERN);
    }

    private static boolean isPutAllMethod(Method method) {
        return method.getName().matches(PUT_ALL_METHOD_PATTERN);
    }

    private static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }
}
