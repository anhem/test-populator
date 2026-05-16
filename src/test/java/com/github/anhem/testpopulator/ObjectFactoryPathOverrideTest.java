package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectFactoryPathOverrideTest {

    @TempDir
    Path tempDir;

    @Test
    void generatedFileIsPlacedInCustomPath() {
        String customPath = tempDir.resolve("custom-generated-sources").toString();
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactory(true)
                .path(customPath)
                .and()
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        Pojo pojo = populateFactory.populate(Pojo.class);

        assertThat(Path.of(customPath)).isDirectory();
        assertGeneratedCode(pojo, populateConfig);
    }
}
