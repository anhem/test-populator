package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.internal.value.TypeSupplier;

import java.util.Collections;
import java.util.Set;

/**
 * For overriding creation/population of classes that cannot be created automatically
 *
 * @param <T> class to override
 */
public interface OverridePopulate<T> extends TypeSupplier<T> {

    /**
     * Implementation required _only_ if ObjectFactory is used. ObjectFactory is not enabled by default.
     *
     * @return a string representation of how to create an object of T.
     * Examples:
     * "UUID.fromString("156585fd-4fe5-4ed4-8d59-d8d70d8b96f5")"
     * "new MyFutureDate(LocalDate.of(3000, 1, 1))"
     * "new MyUUID(java.util.UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\").toString())"
     */
    default String createCode() {
        throw new ObjectException(String.format("createCode() is not implemented for class %s", create().getClass().getName()));
    }

    /**
     * Implementation required _only_ if ObjectFactory is used and you need to provide additional methods
     * to the generated class.
     *
     * @return a set of method definitions to be included in the generated class.
     */
    default Set<String> createMethods() {
        return Collections.emptySet();
    }

    /**
     * Implementation required _only_ if ObjectFactory is used and you need to provide additional imports
     * to the generated class.
     *
     * @return a set of imports to be included in the generated class.
     */
    default Set<String> createImports() {
        return Collections.emptySet();
    }

    /**
     * Implementation required _only_ if ObjectFactory is used and you need to provide additional static imports
     * to the generated class.
     *
     * @return a set of static imports to be included in the generated class.
     */
    default Set<String> createStaticImports() {
        return Collections.emptySet();
    }
}

