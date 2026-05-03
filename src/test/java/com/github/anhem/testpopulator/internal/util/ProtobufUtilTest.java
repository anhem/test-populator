package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.proto.complex.UserProfile;
import com.google.protobuf.Int32Value;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.getMethodsForProtobufBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class ProtobufUtilTest {

    @Test
    void getMethodsForProtobufBuilderReturnsListOfMethods() {
        assertThat(getMethodsForProtobufBuilder(UserProfile.Builder.class, Set.of())).hasSize(12);
        assertThat(getMethodsForProtobufBuilder(Int32Value.Builder.class, Set.of())).hasSize(1);
    }
}