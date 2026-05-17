package com.github.anhem.testpopulator.internal.object;

public enum CodeTemplate {
    CONSTRUCTOR("%1$s %2$s %4$s = new %2$s(%7$s);"),
    STATIC_METHOD("%1$s %2$s %4$s = %5$s.%6$s(%7$s);"),
    BUILDER("%1$s %2$s %4$s = %2$s.%6$s()"),
    SETTER("%1$s %2$s %4$s = new %2$s();"),
    COLLECTION("%1$s %2$s %4$s = new %2$s();"),
    TYPED_COLLECTION("%1$s %2$s<%3$s> %4$s = new %2$s<>();"),
    ENUM_SET("%1$s %2$s<%3$s> %4$s = EnumSet.noneOf(%5$s.class);"),
    ENUM_MAP("%1$s %2$s<%3$s> %4$s = new EnumMap<>(%5$s.class);"),
    VALUE("%1$s %2$s %4$s = %7$s;"),
    ARRAY("%1$s %2$s[] %4$s = new %2$s[]{%7$s};"),
    IMMUTABLE("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s);"),
    MAP_ENTRY("%1$s %2$s<%3$s> %4$s = new %5$s.SimpleEntry<>(%7$s);"),
    OPTIONAL("%1$s %2$s<%3$s> %4$s = %5$s.ofNullable(%7$s);"),
    STREAM("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s);"),
    NUMBER_STREAM("%1$s %2$s %4$s = %5$s.of(%7$s);"),
    ITERATOR("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s).filter(Objects::nonNull).iterator();"),
    ITERABLE("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s).filter(Objects::nonNull).collect(Collectors.toList());"),
    SCANNER("%1$s %2$s %4$s = new %5$s(%7$s);"),
    FUTURE("%1$s %2$s<%3$s> %4$s = %5$s.completedFuture(%7$s);");

    private final String format;

    CodeTemplate(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public String render(Object... args) {
        return String.format(format, args);
    }
}
