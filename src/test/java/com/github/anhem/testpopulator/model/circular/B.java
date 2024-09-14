package com.github.anhem.testpopulator.model.circular;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class B {
    private C c;
    private String string;
}
