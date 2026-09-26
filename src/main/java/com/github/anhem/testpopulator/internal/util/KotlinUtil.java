package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.MethodType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
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

    public static <T> boolean isKotlinSpecialType(Class<T> clazz, boolean kotlinSupport) {
        return kotlinSupport && (isKotlinSingleton(clazz) || hasKotlinCompanion(clazz) || isKotlinValueClass(clazz) || isKotlinSealedClass(clazz));
    }

    public static boolean isKotlinValueClass(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .anyMatch(KotlinUtil::isValueClassConstructor);
    }

    public static Method getKotlinValueClassConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(KotlinUtil::isValueClassConstructor)
                .findFirst()
                .orElse(null);
    }

    private static boolean isValueClassConstructor(Method method) {
        return Modifier.isStatic(method.getModifiers()) &&
                (method.getName().equals("constructor-impl") || method.getName().equals("constructor_impl")) &&
                method.getReturnType().equals(method.getDeclaringClass());
    }

    public static boolean isKotlinSealedClass(Class<?> clazz) {
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        return Arrays.stream(clazz.getDeclaredClasses())
                .anyMatch(c -> clazz.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()));
    }

    public static List<Class<?>> getKotlinSealedSubclasses(Class<?> clazz) {
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            return Collections.emptyList();
        }
        return Arrays.stream(clazz.getDeclaredClasses())
                .filter(c -> clazz.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()))
                .collect(Collectors.toList());
    }

    static <T> boolean isMatchingFactoryMethod(Method method, Class<T> clazz) {
        return method.getReturnType().equals(clazz) &&
                hasAtLeastOneParameter(method) &&
                !hasSelfReferencingParameter(method, clazz);
    }

    public static String getCompanionMethodName(Method companionMethod) {
        return "Companion." + companionMethod.getName();
    }

    public static <T> Set<Integer> detectDefaultedParameters(
            Constructor<T> syntheticCtor,
            Constructor<T> primaryCtor,
            com.github.anhem.testpopulator.internal.carrier.ClassCarrier<T> carrier,
            com.github.anhem.testpopulator.internal.populate.Populator populator,
            int realParamCount,
            int maskCount) {
        try {
            com.github.anhem.testpopulator.internal.object.ObjectFactory dummyFactory = new com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid();
            com.github.anhem.testpopulator.internal.carrier.ClassCarrier<T> dummyCarrier = carrier.mutateObjectFactory(dummyFactory);

            Object[] validArgs = new Object[realParamCount];
            for (int i = 0; i < realParamCount; i++) {
                validArgs[i] = populator.populate(dummyCarrier.createChild(primaryCtor.getParameters()[i]));
            }

            Object[] baseArgs = Arrays.copyOf(validArgs, realParamCount + maskCount + 1);
            for (int i = 0; i < maskCount; i++) {
                baseArgs[realParamCount + i] = 0;
            }
            baseArgs[baseArgs.length - 1] = null;

            T baseObj = syntheticCtor.newInstance(baseArgs);

            Set<Integer> defaultedIndices = new HashSet<>();
            for (int i = 0; i < realParamCount; i++) {
                Object[] testArgs = baseArgs.clone();
                int maskIndex = i / 32;
                int bitPosition = i % 32;
                testArgs[realParamCount + maskIndex] = (1 << bitPosition);

                T testObj = syntheticCtor.newInstance(testArgs);

                if (!objectsAreEqual(baseObj, testObj)) {
                    defaultedIndices.add(i);
                }
            }
            return defaultedIndices;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private static boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            return obj1 == obj2;
        }
        try {
            if (obj1.equals(obj2) && obj1.getClass().getMethod("equals", Object.class).getDeclaringClass() != Object.class) {
                return true;
            }
            for (Field field : obj1.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                if (!Objects.equals(field.get(obj1), field.get(obj2))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getDefaultValueForType(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        return null;
    }
}
