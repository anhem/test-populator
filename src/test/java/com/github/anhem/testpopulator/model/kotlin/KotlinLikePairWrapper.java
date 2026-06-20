package com.github.anhem.testpopulator.model.kotlin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KotlinLikePairWrapper {
    private final KotlinLikePair<String, Integer> pair;
}
