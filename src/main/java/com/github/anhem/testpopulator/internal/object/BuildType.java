package com.github.anhem.testpopulator.internal.object;

public enum BuildType {
    CONSTRUCTOR,
    SETTER,
    MUTATOR,
    BUILDER,
    METHOD,
    STATIC_METHOD,
    SET,
    SET_OF,
    LIST,
    LIST_OF,
    MAP,
    MAP_OF,
    MAP_ENTRY,
    ARRAY,
    OPTIONAL,
    VALUE;

    public boolean isParameterizedType() {
        switch (this) {
            case SET:
            case SET_OF:
            case LIST:
            case LIST_OF:
            case MAP:
            case MAP_OF:
            case OPTIONAL:
                return true;
            default:
                return false;
        }
    }
}
