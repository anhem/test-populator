package com.github.anhem.testpopulator.model.kotlin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JavaClassWithKotlinField {
    private final KotlinLikeClass kotlinLikeClass;
    private final KotlinLikeClass.InnerClass innerClass;
}
