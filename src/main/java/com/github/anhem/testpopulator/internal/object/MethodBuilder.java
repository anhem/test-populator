package com.github.anhem.testpopulator.internal.object;

import java.util.List;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.METHOD;

public class MethodBuilder extends ObjectBuilder {

    public MethodBuilder(String name, int expectedChildren) {
        super(null, name, METHOD, false, expectedChildren);
    }

    @Override
    public List<String> build() {
        return buildChildren().collect(java.util.stream.Collectors.toList());
    }

    @Override
    protected Stream<String> getInstantiationLine(List<ObjectBuilder> argumentChildren) {
        return Stream.empty();
    }
}
