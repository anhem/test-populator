package com.github.anhem.testpopulator.model.java.stc;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class UserId {
    String value;
}
