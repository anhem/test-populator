package com.github.anhem.testpopulator.model.java.stc;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@AllArgsConstructor(staticName = "of")
public class User {
    UserId userId;
    String firstName;
    String lastName;
}

