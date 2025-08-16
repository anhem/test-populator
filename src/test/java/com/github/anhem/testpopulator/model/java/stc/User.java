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

    public static User from(User user, UserId userId) {
        return User.of(userId, user.firstName, user.lastName);
    }
}

