package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class KotlinLikePair<A, B> {
    private final A first;
    private final B second;

    public KotlinLikePair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A component1() {
        return first;
    }

    public B component2() {
        return second;
    }

    public KotlinLikePair<A, B> copy(A first, B second) {
        return new KotlinLikePair<>(first, second);
    }
}
