package com.github.anhem.testpopulator.internal;

import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

public interface PopulatingStrategy {
    <T> T populate(ClassCarrier<T> classCarrier, Populator populator);
}
