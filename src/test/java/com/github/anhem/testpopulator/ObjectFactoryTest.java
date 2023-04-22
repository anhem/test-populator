package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ObjectFactoryTest {

    @Test
    void test1() {
        new PopulateFactory().populate(MyParentClass.class);
    }

    @Test
    void test2() {
        AllArgsConstructor populate = new PopulateFactory().populate(AllArgsConstructor.class);
    }

    private static class MyParentClass {
        private final MyClass myClass;
        private final MyClass myClass2;
        private final Integer integer;
        private final MyClass myClass3;

        public MyParentClass(MyClass myClass, MyClass myClass2, Integer integer, MyClass myClass3) {
            this.myClass = myClass;
            this.myClass2 = myClass2;
            this.integer = integer;
            this.myClass3 = myClass3;
        }
    }

    private static class MyClass {
        private final String s;
        private final List<Integer> integers;

        public MyClass(String s, List<Integer> integers) {
            this.s = s;
            this.integers = integers;
        }
    }
}
