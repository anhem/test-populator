package com.github.anhem.testpopulator.util;

import com.github.anhem.testpopulator.ObjectResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static com.github.anhem.testpopulator.util.FileWriterUtil.*;
import static java.io.File.createTempFile;
import static java.nio.file.Files.readAllLines;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FileWriterUtilTest {

    public static final Set<String> IMPORTS = Set.of("java.util.ArrayList");
    public static final Set<String> STATIC_IMPORTS = Set.of("com.github.anhem.testpopulator.model.java.ArbitraryEnum.A");
    public static final List<String> OBJECTS = List.of(
            "public static final ArrayList<ArbitraryEnum> arrayList0 = new ArrayList<>();",
            "static {",
            "arrayList0.add(\"A\")",
            "}"
    );
    public static final ObjectResult OBJECT_RESULT = new ObjectResult(
            FileWriterUtilTest.class.getPackageName(),
            FileWriterUtilTest.class.getName(),
            IMPORTS,
            STATIC_IMPORTS,
            OBJECTS
    );

    @Test
    void getPathReturnsPathBuiltFromObjectResultAndPopulateConfig() {
        Path path = FileWriterUtil.getPath(OBJECT_RESULT, DEFAULT_POPULATE_CONFIG);

        assertThat(path).hasToString("target/generated-test-sources/test-populator/com/github/anhem/testpopulator/util/com.github.anhem.testpopulator.util.FileWriterUtilTest_89c4233557e488fd2e99bcfcac1b5dc9.java");
    }

    @Test
    void createOrOverwriteFileRunsWithoutExceptions() throws IOException {
        createOrOverwriteFile(getTempPath());

        assertThat(getTempPath().toFile()).exists();
    }

    @Test
    void writePackageAddsPackageToFile() throws IOException {
        Path path = getTempPath();

        writePackage(OBJECT_RESULT, path);

        assertThat(readAllLines(path)).isEqualTo(List.of(
                "package com.github.anhem.testpopulator.util;",
                ""
        ));
    }

    @Test
    void writeImportAddsImportsToFile() throws IOException {
        Path path = getTempPath();

        writeImports(OBJECT_RESULT, path);

        assertThat(readAllLines(path)).isEqualTo(List.of(
                "import java.util.ArrayList;",
                ""
        ));
    }

    @Test
    void writeStaticImportAddsStaticImportsToFile() throws IOException {
        Path path = getTempPath();

        writeStaticImports(OBJECT_RESULT, path);

        assertThat(readAllLines(path)).isEqualTo(List.of(
                "import static com.github.anhem.testpopulator.model.java.ArbitraryEnum.A;",
                ""
        ));
    }

    @Test
    void writeStartClassAddsClassToFile() throws IOException {
        Path path = getTempPath();

        writeStartClass(OBJECT_RESULT, path, DEFAULT_POPULATE_CONFIG);

        assertThat(readAllLines(path)).isEqualTo(List.of(
                "public class com.github.anhem.testpopulator.util.FileWriterUtilTest_89c4233557e488fd2e99bcfcac1b5dc9 {",
                ""
        ));
    }

    @Test
    void writeEndClassAddsEndClassToFile() throws IOException {
        Path path = getTempPath();

        writeEndClass(path);

        assertThat(readAllLines(path)).isEqualTo(List.of("}"));
    }

    @Test
    void writeObjectsAddsObjectsToFile() throws IOException {
        Path path = getTempPath();

        writeObjects(OBJECT_RESULT, path);

        assertThat(readAllLines(path)).isEqualTo(List.of(
                "	public static final ArrayList<ArbitraryEnum> arrayList0 = new ArrayList<>();",
                "",
                "	static {",
                "		arrayList0.add(\"A\")",
                "	}",
                ""));
    }

    private static Path getTempPath() throws IOException {
        return createTempFile("temp_", ".tmp").toPath();
    }

}