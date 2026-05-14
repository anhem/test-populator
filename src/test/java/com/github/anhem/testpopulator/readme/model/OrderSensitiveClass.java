package com.github.anhem.testpopulator.readme.model;

public class OrderSensitiveClass {

    private String name;

    /**
     * An interface with no concrete implementation and no override configured.
     * This will cause the CONSTRUCTOR strategy to fail if this constructor is chosen.
     */
    public interface Unpopulatable {
    }

    /**
     * This constructor will be matched by the CONSTRUCTOR strategy.
     * It will fail because 'Unpopulatable' cannot be instantiated.
     */
    public OrderSensitiveClass(String name, Unpopulatable unpopulatable) {
        this.name = name;
    }

    /**
     * This no-arg constructor and the setter will be matched by the SETTER strategy.
     * This will succeed even if 'Unpopulatable' is unpopulatable, because it's not used here.
     */
    public OrderSensitiveClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
