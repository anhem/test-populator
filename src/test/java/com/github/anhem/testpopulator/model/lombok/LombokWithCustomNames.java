package com.github.anhem.testpopulator.model.lombok;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "customBuilder", buildMethodName = "customBuild")
public class LombokWithCustomNames {
    private final String name;
    private final int age;
}
