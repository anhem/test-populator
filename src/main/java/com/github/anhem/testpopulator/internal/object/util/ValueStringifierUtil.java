package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.internal.object.ValueFormatter;
import com.github.anhem.testpopulator.internal.util.KotlinUtil;

public class ValueStringifierUtil {

    public static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";

    private ValueStringifierUtil() {
    }

    public static String stringify(Object value, Class<?> clazz, String name, PopulateConfig config, ObjectBuilder objectBuilder) {
        if (value.getClass().isEnum()) {
            return value.toString();
        }

        if (KotlinUtil.isKotlinSingleton(clazz, config.isKotlinSupport())) {
            return config.isKotlinSupport() ? clazz.getSimpleName() : String.format("%s.INSTANCE", clazz.getSimpleName());
        }

        if (name != null) {
            OverrideTarget target = OverrideTarget.of(name, clazz);
            OverridePopulate<?> nameOverride = config.getNameOverrides().get(target);
            if (nameOverride != null && isCreateCodeOverridden(nameOverride)) {
                return applyOverride(nameOverride, objectBuilder);
            }
        }

        OverridePopulate<?> classOverride = config.getClassOverrides().get(clazz);
        if (classOverride != null && isCreateCodeOverridden(classOverride)) {
            return applyOverride(classOverride, objectBuilder);
        }

        String formatted = ValueFormatter.format(value, clazz);
        if (formatted != null) {
            if (config.isKotlinSupport() && formatted.startsWith("new ")) {
                return formatted.substring(4);
            }
            return formatted;
        }

        if (name != null) {
            OverridePopulate<?> nameOverride = config.getNameOverrides().get(OverrideTarget.of(name, clazz));
            if (nameOverride != null) {
                return nameOverride.createCode();
            }
        }

        if (classOverride != null) {
            return classOverride.createCode();
        }

        throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
    }

    private static boolean isCreateCodeOverridden(OverridePopulate<?> overridePopulate) {
        try {
            return !overridePopulate.getClass().getMethod("createCode").getDeclaringClass().equals(OverridePopulate.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static String applyOverride(OverridePopulate<?> overridePopulate, ObjectBuilder objectBuilder) {
        objectBuilder.addMethods(overridePopulate.createMethods());
        objectBuilder.addImports(overridePopulate.createImports());
        objectBuilder.addStaticImports(overridePopulate.createStaticImports());
        return overridePopulate.createCode();
    }
}
