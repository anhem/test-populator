package com.github.anhem.testpopulator.model.java;

import java.util.ArrayList;
import java.util.HashMap;

public class NestedCollections {


    private HashMap<ArrayList<SimpleClass>, String> rofl2;

    public NestedCollections(HashMap<ArrayList<SimpleClass>, String> rofl2) {
        this.rofl2 = rofl2;
    }

    public static class SimpleClass {
        private String string;

        public SimpleClass() {
        }

        public SimpleClass(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
