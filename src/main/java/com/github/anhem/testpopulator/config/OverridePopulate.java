package com.github.anhem.testpopulator.config;

/**
 * For overriding creation/population of classes that cannot be created automatically
 *
 * @param <T> class to override
 */
public interface OverridePopulate<T> {
    T create();
}
