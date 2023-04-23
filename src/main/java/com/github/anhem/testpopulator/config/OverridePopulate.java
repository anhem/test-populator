package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.PopulateException;

/**
 * For overriding creation/population of classes that cannot be created automatically
 *
 * @param <T> class to override
 */
public interface OverridePopulate<T> {
    T create();

    /**
     * Own implementation required _only_ if ObjectFactory is used.
     *
     * @return a string representation of how to create an object of T.
     * Examples:
     * "UUID.fromString("156585fd-4fe5-4ed4-8d59-d8d70d8b96f5");"
     * "new MyFutureDate(LocalDate.of(3000, 1, 1));"
     */
    default String createString() {
        throw new PopulateException(String.format("createString() is not implemented in class %s", this.getClass().getName()));
    }
}
