package com.github.anhem.testpopulator.config;

public enum Language {
    JAVA("public static final", ".java", "public class", ";"), 
    KOTLIN("val", ".kt", "object", "");
    
    private final String modifier;
    private final String fileExtension;
    private final String classDeclaration;
    private final String statementEnd;
    
    Language(String modifier, String fileExtension, String classDeclaration, String statementEnd) {
        this.modifier = modifier;
        this.fileExtension = fileExtension;
        this.classDeclaration = classDeclaration;
        this.statementEnd = statementEnd;
    }
    
    public String getModifier() {
        return modifier;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getClassDeclaration() {
        return classDeclaration;
    }

    public String getStatementEnd() {
        return statementEnd;
    }

    public static Language fromClass(Class<?> clazz) {
        if (clazz == null) {
            return JAVA;
        }
        return java.util.Arrays.stream(clazz.getDeclaredAnnotations())
                .anyMatch(a -> a.annotationType().getName().equals("kotlin.Metadata"))
                ? KOTLIN : JAVA;
    }
}
