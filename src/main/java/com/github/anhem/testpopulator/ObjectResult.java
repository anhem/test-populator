package com.github.anhem.testpopulator;

import java.util.Collections;
import java.util.List;

public class ObjectResult {

    public static final ObjectResult EMPTY_OBJECT_RESULT = new ObjectResult(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    private final String packageName;
    private final String name;
    private final List<String> imports;
    private final List<String> staticImports;
    private final List<String> objects;

    public ObjectResult(String packageName, String name, List<String> imports, List<String> staticImports, List<String> objects) {
        this.packageName = packageName;
        this.name = name;
        this.imports = imports;
        this.staticImports = staticImports;
        this.objects = objects;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<String> getStaticImports() {
        return staticImports;
    }

    public List<String> getObjects() {
        return objects;
    }

    public boolean isValid() {
        return packageName != null && name != null && !objects.isEmpty();
    }
}
