package com.github.anhem.testpopulator.model.lombok;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;


@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LombokImmutableExtendsLombokAbstractImmutable extends LombokAbstractImmutable {

    String anotherString;
}
