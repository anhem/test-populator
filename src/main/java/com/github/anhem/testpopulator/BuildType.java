package com.github.anhem.testpopulator;

public enum BuildType {
    CONSTRUCTOR,
    SETTER,
    BUILDER,
    METHOD,
    SET,
    SET_OF,
    LIST,
    LIST_OF,
    MAP,
    MAP_OF,
    ARRAY,
    OVERRIDE_VALUE,
    VALUE;

    public boolean isParameterizedType() {
        switch (this) {
            case SET:
            case SET_OF:
            case LIST:
            case LIST_OF:
            case MAP:
            case MAP_OF:
                return true;
            default:
                return false;
        }
    }
}
