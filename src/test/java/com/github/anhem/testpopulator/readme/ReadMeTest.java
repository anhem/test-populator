package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.readme.model.MyClass;
import com.github.anhem.testpopulator.readme.model.MyClass2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadMeTest {

    @Test
    public void myClass() {
        MyClass myClass = new PopulateFactory().populate(MyClass.class);

        assertThat(myClass).hasNoNullFieldsOrProperties();
        System.out.println("myClass = " + myClass);
    }

    @Test
    void myClass2() {
        MyClass2 myClass2 = TestPopulator.populate(MyClass2.class);

        assertThat(myClass2).hasNoNullFieldsOrProperties();
    }

}
