package com.github.anhem.testpopulator.internal.object;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.STATIC_METHOD;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.concatenate;

public class StaticMethodObjectBuilder extends ObjectBuilder {

    public static final String S_S_S_S_S_S = "%s %s %s = %s.%s(%s);";

    private final Class<?> clazz;
    private final String methodName;

    public StaticMethodObjectBuilder(Class<?> clazz, String name, String methodName, int expectedChildren) {
        super(name, STATIC_METHOD, expectedChildren);
        this.clazz = clazz;
        this.methodName = methodName;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean isUseFullyQualifiedName() {
        return false;
    }

    @Override
    public List<String> build() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(S_S_S_S_S_S, PSF, getClassName(), getName(), getClassName(), methodName, buildArguments())))
                .collect(Collectors.toList());
    }

}
