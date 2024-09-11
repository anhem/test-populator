package com.github.anhem.testpopulator.model.java.circular;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class A {
    private String string;
    private B b;
    private Map<C, D> cdMap;
}
