package com.github.anhem.testpopulator.testutil;

import com.github.anhem.testpopulator.model.java.circular.A;
import com.github.anhem.testpopulator.model.java.circular.B;
import com.github.anhem.testpopulator.model.java.circular.C;
import com.github.anhem.testpopulator.model.java.circular.D;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertTestUtil {

    public static <T> void assertRandomlyPopulatedValues(T value_1, T value_2) {
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).hasNoNullFieldsOrProperties();
        assertThat(value_2).hasNoNullFieldsOrProperties();
        assertThat(value_1).usingRecursiveAssertion().hasNoNullFields();
        assertThat(value_2).usingRecursiveAssertion().hasNoNullFields();
        assertThat(value_1).isNotEqualTo(value_2);
        assertThat(value_1).usingRecursiveAssertion().isNotEqualTo(value_2);
    }

    public static void assertRandomlyPopulatedValues(String value_1, String value_2) {
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).isNotEqualTo(value_2);
    }

    public static void assertCircularDependency(A a_1, A a_2) {
        assertThat(a_1).isNotNull();
        assertThat(a_2).isNotNull();
        assertThat(a_1).isNotEqualTo(a_2);
        assertThat(a_1).usingRecursiveAssertion().isNotEqualTo(a_2);
        assertCircularDependency(a_1);
        assertCircularDependency(a_2);
    }

    private static void assertCircularDependency(A a) {
        assertThat(a).hasNoNullFieldsOrProperties();

        B b = a.getB();
        assertThat(b).hasNoNullFieldsOrProperties();

        C c = b.getC();
        assertThat(c).hasNoNullFieldsOrProperties();

        List<C> cs = c.getCs();
        assertThat(cs).isEmpty();

        D d = c.getD();
        assertThat(d).hasAllNullFieldsOrProperties();

        C cdKey = a.getCdMap().keySet().iterator().next();
        assertThat(cdKey).hasNoNullFieldsOrProperties();
        assertThat(cdKey.getCs()).isEmpty();

        D cdKeyD = cdKey.getD();
        assertThat(cdKeyD.getA()).isNull();

        B cdKeyDB = cdKeyD.getB();
        assertThat(cdKeyDB.getC()).isNull();
        assertThat(cdKeyDB.getString()).isNotNull();

        assertThat(cdKeyD.getC()).isNull();

        D cdValue = a.getCdMap().values().iterator().next();
        assertThat(cdValue.getA()).isNull();

        B cdValueB = cdValue.getB();

        C cdValueBC = cdValueB.getC();
        assertThat(cdValueBC.getD()).isNull();
        assertThat(cdValueBC.getCs()).isEmpty();

        assertThat(cdValueB.getString()).isNotNull();
    }

}
