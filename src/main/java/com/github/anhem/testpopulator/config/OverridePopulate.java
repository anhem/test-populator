package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.internal.value.TypeSupplier;

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
    default String createString() {
        throw new ObjectException(String.format("createString() is not implemented for class %s", create().getClass().getName()));
    }
}

