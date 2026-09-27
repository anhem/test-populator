package com.github.anhem.testpopulator.internal.object;

public enum CodeTemplate {
    CONSTRUCTOR("%1$s %2$s %4$s = new %2$s(%7$s);", "%1$s %4$s: %2$s = %2$s(%7$s)"),
    PRIVATE_CONSTRUCTOR("%1$s %2$s %4$s = %6$s(%7$s);", "%1$s %4$s: %2$s = %6$s(%7$s)"),
    FIELD("%1$s %2$s %4$s = %6$s(%7$s);", "%1$s %4$s: %2$s = %6$s(%7$s)"),
    STATIC_METHOD("%1$s %2$s %4$s = %5$s.%6$s(%7$s);", "%1$s %4$s: %2$s = %5$s.%6$s(%7$s)"),
    BUILDER("%1$s %2$s %4$s = %2$s.%6$s()", "%1$s %4$s: %2$s = %2$s.%6$s()"),
    SETTER("%1$s %2$s %4$s = new %2$s();", "%1$s %4$s: %2$s = %2$s()"),
    COLLECTION("%1$s %2$s %4$s = new %2$s();", "%1$s %4$s = java.util.%2$s()"),
    TYPED_COLLECTION("%1$s %2$s<%3$s> %4$s = new %2$s<>();", "%1$s %4$s = java.util.%2$s<%3$s>()"),
    ENUM_SET("%1$s %2$s<%3$s> %4$s = EnumSet.noneOf(%5$s.class);", "%1$s %4$s = java.util.EnumSet.noneOf(%5$s::class.java)"),
    ENUM_MAP("%1$s %2$s<%3$s> %4$s = new EnumMap<>(%5$s.class);", "%1$s %4$s = java.util.EnumMap<Any, Any>(%5$s::class.java)"),
    VALUE("%1$s %2$s %4$s = %7$s;", "%1$s %4$s: %2$s = %7$s"),
    ARRAY("%1$s %2$s[] %4$s = new %2$s[]{%7$s};", "%1$s %4$s: Array<%2$s> = arrayOf(%7$s)"),
    IMMUTABLE("%1$s %2$s<%3$s> %4$s = %5$s.%6$s(%7$s);", "%1$s %4$s = %6$s(%7$s)"),
    MAP_ENTRY("%1$s %2$s<%3$s> %4$s = new %5$s.SimpleEntry<>(%7$s);", "%1$s %4$s = java.util.AbstractMap.SimpleEntry(%7$s)"),
    OPTIONAL("%1$s %2$s<%3$s> %4$s = %5$s.ofNullable(%7$s);", "%1$s %4$s = java.util.Optional.ofNullable(%7$s)"),
    STREAM("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s);", "%1$s %4$s = java.util.stream.Stream.of(%7$s)"),
    NUMBER_STREAM("%1$s %2$s %4$s = %5$s.of(%7$s);", "%1$s %4$s = java.util.stream.%5$s.of(%7$s)"),
    ITERATOR("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s).filter(Objects::nonNull).iterator();", "%1$s %4$s = java.util.stream.Stream.of(%7$s).filter { it != null }.iterator()"),
    ITERABLE("%1$s %2$s<%3$s> %4$s = %5$s.of(%7$s).filter(Objects::nonNull).collect(Collectors.toList());", "%1$s %4$s = java.util.stream.Stream.of(%7$s).filter { it != null }.collect(java.util.stream.Collectors.toList())"),
    SCANNER("%1$s %2$s %4$s = new %5$s(%7$s);", "%1$s %4$s: %2$s = %5$s(%7$s)"),
    FUTURE("%1$s %2$s<%3$s> %4$s = %5$s.completedFuture(%7$s);", "%1$s %4$s: %2$s<%3$s> = %5$s.completedFuture(%7$s)");

    private final String format;
    private final String kotlinFormat;

    CodeTemplate(String format, String kotlinFormat) {
        this.format = format;
        this.kotlinFormat = kotlinFormat;
    }

    public String getFormat() {
        return format;
    }

    public String getFormat(boolean isKotlinSupport) {
        return isKotlinSupport ? kotlinFormat : format;
    }

    public String render(boolean isKotlinSupport, Object... args) {
        return String.format(getFormat(isKotlinSupport), args);
    }
}
