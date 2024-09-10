package com.github.anhem.testpopulator.model.circular;

import java.util.List;

public class C {

    private final D d;
    private final List<C> cs;

    public C(D d, List<C> cs) {
        this.d = d;
        this.cs = cs;
    }
}
