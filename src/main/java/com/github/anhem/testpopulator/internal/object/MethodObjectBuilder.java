package com.github.anhem.testpopulator.internal.object;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.internal.object.BuildType.METHOD;

public class MethodObjectBuilder extends ObjectBuilder {

    public MethodObjectBuilder(String name, int expectedChildren) {
        super(name, METHOD, expectedChildren);
    }

    @Override
    public Class<?> getClazz() {
        return null;
    }

    @Override
    public boolean isUseFullyQualifiedName() {
        return false;
    }

    @Override
    public List<String> build() {
        return buildChildren().collect(Collectors.toList());
    }

}
