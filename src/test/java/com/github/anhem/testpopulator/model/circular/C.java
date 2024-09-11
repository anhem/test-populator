package com.github.anhem.testpopulator.model.circular;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class C {
    private D d;
    private List<C> cs;
}
