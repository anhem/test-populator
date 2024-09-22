package com.github.anhem.testpopulator.internal.value;

@FunctionalInterface
public interface TypeSupplier<T> {

    /**
     * An implementation that creates an object of type T
     *
     * @return object of type T
     */
    T create();
}
