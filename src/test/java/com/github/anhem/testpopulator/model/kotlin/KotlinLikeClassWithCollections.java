package com.github.anhem.testpopulator.model.kotlin;

import java.util.List;
import java.util.Map;

/**
 * Simulates a Kotlin data class: class MyClassWithCollections(val strings: List<String>, val map: Map<String, Int>)
 * Used to verify that Kotlin code generation produces mapOf(key to value) instead of mapOf(key, value).
 */
public class KotlinLikeClassWithCollections {

    private final List<String> strings;
    private final Map<String, Integer> map;

    public KotlinLikeClassWithCollections(List<String> strings, Map<String, Integer> map) {
        this.strings = strings;
        this.map = map;
    }

    public List<String> getStrings() {
        return strings;
    }

    public Map<String, Integer> getMap() {
        return map;
    }
}
