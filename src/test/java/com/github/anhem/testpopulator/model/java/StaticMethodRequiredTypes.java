package com.github.anhem.testpopulator.model.java;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StaticMethodRequiredTypes {

    private OptionalInt optionalInt;
    private OptionalLong optionalLong;
    private OptionalDouble optionalDouble;

}
