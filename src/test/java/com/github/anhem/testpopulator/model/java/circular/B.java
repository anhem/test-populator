package com.github.anhem.testpopulator.model.java.circular;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class B {
    private C c;
    private String string;
}
