package com.github.anhem.testpopulator.internal;

public class PopulatorExceptionMessages {

    public static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection '%s'";
    public static final String NO_MATCHING_STRATEGY = "Unable to populate '%s'. No matching strategy found. Tried with %s. Try another strategy or override population for this class";
    public static final String FAILED_TO_SET_FIELD = "Failed to set field '%s' in object of class %s";
    public static final String FAILED_TO_CALL_METHOD = "Failed to call method '%s' in object of class '%s'";
    public static final String FAILED_TO_CREATE_OBJECT = "Failed to create object of '%s' using '%s' strategy";
    public static final String FAILED_TO_CREATE_COLLECTION = "Failed to create and populate collection '%s'";
    public static final String FAILED_TO_CALL_STATIC_METHOD = "Failed to call static method '%s' of class '%s'";

    private PopulatorExceptionMessages() {

    }
}
