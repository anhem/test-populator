package com.github.anhem.testpopulator.model.java.setter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class OddPojo {

    private Pojo pojo;
    private List<Pojo> pojoList;
    private List<List<Pojo>> listOfPojoLists;
    private Set<Pojo> pojoSet;
    private Set<Set<Pojo>> setOfPojoSets;
    private Map<Pojo, Pojo> pojoToPojoMap;
    private Map<String, List<Pojo>> mapOfStringToPojoList;
    private Map<List<String>, List<Pojo>> mapOfStringListToPojoMap;
    private List<List<List<Pojo>>> weirdPojoList;
    private List<DateAndTimeMix> dateAndTimeMixes;

}
