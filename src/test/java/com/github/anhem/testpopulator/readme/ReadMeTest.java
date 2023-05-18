package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.readme.model.MyClass;
import com.github.anhem.testpopulator.readme.model.MyClass2;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadMeTest {

    @Test
    void myClass() {
        MyClass myClass = new PopulateFactory().populate(MyClass.class);

        assertThat(myClass).hasNoNullFieldsOrProperties();
        assertThat(myClass).extracting("myInnerClass.integer").isNotNull();
        assertThat(myClass).extracting("myInnerClass.stringToLocalDateMap").is(new Condition<>(m -> ((Map<?, ?>) m).size() > 0, "not empty"));
        System.out.println("myClass = " + myClass);
    }

    @Test
    void myClass2() {
        MyClass2 myClass2 = TestPopulator.populate(MyClass2.class);

        assertThat(myClass2).hasNoNullFieldsOrProperties();
        assertThat(myClass2).extracting("myInnerClass.integer").isNotNull();
        assertThat(myClass2).extracting("myInnerClass.stringToLocalDateMap").is(new Condition<>(o -> ((Map<?, ?>) o).size() > 0, "not empty"));
    }

}
