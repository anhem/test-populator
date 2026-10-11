package com.github.anhem.testpopulator.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MutatorGetter {

    private final Method getter;
    private final Object targetObject;
    private final List<Method> childMutatorMethods;
    private final List<Field> childPublicFields;
    private final List<MutatorGetter> childMutatorGetters;

    public MutatorGetter(
            Method getter,
            Object targetObject,
            List<Method> childMutatorMethods,
            List<Field> childPublicFields,
            List<MutatorGetter> childMutatorGetters
    ) {
        this.getter = getter;
        this.targetObject = targetObject;
        this.childMutatorMethods = childMutatorMethods;
        this.childPublicFields = childPublicFields;
        this.childMutatorGetters = childMutatorGetters;
    }

    public Method getGetter() {
        return getter;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public List<Method> getChildMutatorMethods() {
        return childMutatorMethods;
    }

    public List<Field> getChildPublicFields() {
        return childPublicFields;
    }

    public List<MutatorGetter> getChildMutatorGetters() {
        return childMutatorGetters;
    }

    public int getExpectedChildren() {
        return childMutatorMethods.size() + childPublicFields.size() + childMutatorGetters.size();
    }
}
