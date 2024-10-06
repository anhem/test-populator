package com.github.anhem.testpopulator.model.circular;

import lombok.*;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class C {
    private D d;
    private List<C> cList;
    private Set<C> cSet;
    private Map<String, C> cMap;
    private ArrayList<C> cArrayList;
    private HashSet<C> cHashSet;
    private HashMap<String, C> cHashMap;
    private Map.Entry<String, C> entry1;
    private Map.Entry<C, String> entry2;
}
