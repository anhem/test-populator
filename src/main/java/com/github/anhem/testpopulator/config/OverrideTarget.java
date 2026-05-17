package com.github.anhem.testpopulator.config;

import java.util.Objects;

/**
 * A target for an override, defined by a name and a class.
 */
public class OverrideTarget {
    private final String name;
    private final Class<?> clazz;

    private OverrideTarget(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * Create a new override target.
     *
     * @param name  the name of the target (e.g. field name or method name)
     * @param clazz the class of the target
     * @return a new override target
     */
    public static OverrideTarget of(String name, Class<?> clazz) {
        return new OverrideTarget(name, clazz);
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverrideTarget that = (OverrideTarget) o;
        return Objects.equals(name, that.name) && Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clazz);
    }

    @Override
    public String toString() {
        return "OverrideTarget{" +
                "name='" + name + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
