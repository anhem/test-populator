package com.github.anhem.testpopulator.model.java.stc;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor
public class UserGroup {

    List<User> users;

    public static UserGroup from(User... users) {
        return new UserGroup(Arrays.asList(users));
    }
}
