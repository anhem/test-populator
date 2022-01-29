package com.github.anhem.testpopulator.model.immutables;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
public interface ImmutablesOddInterface {

    ImmutablesAbstract getImmutablesAbstract();

    ImmutableImmutablesAbstract getImmutableImmutablesAbstract();

    ImmutablesInterface getImmutablesInterface();

    ImmutableImmutablesInterface getImmutableImmutablesInterface();

    List<ImmutablesInterface> getImmutablesInterfaces();

    Map<ImmutablesInterface, ImmutableImmutablesAbstract> getImmutablesInterfaceImmutableImmutablesAbstractMap();

}
