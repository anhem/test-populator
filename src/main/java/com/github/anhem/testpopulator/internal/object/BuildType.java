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
    ENUM_SET,
    LIST,
    LIST_OF,
    MAP,
    MAP_OF,
    ENUM_MAP,
    MAP_ENTRY,
    ARRAY,
    OPTIONAL,
    VALUE;

    public boolean isParameterizedType() {
        switch (this) {
            case SET:
            case SET_OF:
            case ENUM_SET:
            case LIST:
            case LIST_OF:
            case MAP:
            case MAP_OF:
            case ENUM_MAP:
            case MAP_ENTRY:
            case OPTIONAL:
                return true;
            default:
                return false;
        }
    }
}
