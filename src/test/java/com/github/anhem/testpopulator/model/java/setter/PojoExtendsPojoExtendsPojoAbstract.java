package com.github.anhem.testpopulator.model.java.setter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PojoExtendsPojoExtendsPojoAbstract extends PojoExtendsPojoAbstract {

    private Integer anotherInteger;

}
