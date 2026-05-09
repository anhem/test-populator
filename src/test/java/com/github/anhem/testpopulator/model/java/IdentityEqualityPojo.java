package com.github.anhem.testpopulator.model.java;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentityEqualityPojo {

    private StringBuilder stringBuilder;
    private StringBuffer stringBuffer;
    private Throwable throwable;
    private Exception exception;
    private RuntimeException runtimeException;
    private Error error;

}
