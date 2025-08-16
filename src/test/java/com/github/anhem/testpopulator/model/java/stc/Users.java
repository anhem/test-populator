package com.github.anhem.testpopulator.model.java.stc;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "with")
public class Users {
    List<User> users;

    public static Users empty() {
        return new Users(List.of());
    }

    public static Users ofTwo(User user1, User user2) {
        return new Users(List.of(user1, user2));
    }

    public static Users of(User... users) {
        return new Users(Arrays.asList(users));
    }
}
