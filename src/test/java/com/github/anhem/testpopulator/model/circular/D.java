package com.github.anhem.testpopulator.model.circular;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class D {
    private A a;
    private B b;
    private C c;
}
