package com.github.anhem.testpopulator;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

class ObjectResult {

    public static final ObjectResult EMPTY_OBJECT_RESULT = new ObjectResult(null, null, emptySet(), emptySet(), emptyList());

    private final String packageName;
    private final String className;
    private final Set<String> imports;
    private final Set<String> staticImports;
    private final List<String> objects;

    public ObjectResult(String packageName, String className, Set<String> imports, Set<String> staticImports, List<String> objects) {
        this.packageName = packageName;
        this.className = className;
        this.imports = imports;
        this.staticImports = staticImports;
        this.objects = objects;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getImports() {
        return imports;
    }

    public Set<String> getStaticImports() {
        return staticImports;
    }

    public List<String> getObjects() {
        return objects;
    }

    public boolean isValid() {
        return packageName != null && className != null && !objects.isEmpty();
    }
}
