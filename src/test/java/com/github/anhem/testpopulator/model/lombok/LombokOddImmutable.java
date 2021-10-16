package com.github.anhem.testpopulator.model.lombok;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class LombokOddImmutable {

    @Singular
    List<LombokImmutable> singularLombokImmutables;
    List<LombokImmutable> lombokImmutables;
    @Singular
    List<List<List<LombokImmutable>>> singularWeirdLombokImmutables;
    List<List<List<LombokImmutable>>> weirdLombokImmutables;

    ArrayList<HashMap<String, LombokImmutable>> arrayListWithHashMap;
    ArrayList<HashMap<String, ArrayList<LombokImmutable>>> arrayListWithHashMapWithArrayList;

    HashMap<Integer, ArrayList<String>> hashMapWithArrayList;

}
