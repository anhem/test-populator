package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class KotlinLikeWithGenerics {

    private final String name;
    private final List<String> tags;

    public KotlinLikeWithGenerics(String name, List<String> tags) {
        this.name = name;
        this.tags = tags;
    }

    public KotlinLikeWithGenerics(String name, List tags, int mask, DefaultConstructorMarker marker) {
        this.name = name;
        this.tags = (mask & 2) != 0 ? Collections.emptyList() : tags;
    }
}
