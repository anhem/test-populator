package com.github.anhem.testpopulator.readme.model;

import java.net.http.HttpClient;

public class MyNestedStaticMethodClass {
    private HttpClient value;

    public MyNestedStaticMethodClass() {
    }

    public HttpClient getValue() {
        return value;
    }

    public void setValue(HttpClient value) {
        this.value = value;
    }
}
