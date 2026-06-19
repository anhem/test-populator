package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.FormattingContext;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.isBasicValue;
import static java.util.stream.Collectors.joining;

public class ArgumentFormatterUtil {

    public static final String ARGUMENT_DELIMITER = ", ";
    private static final String NULL = ObjectBuilder.NULL;

    private ArgumentFormatterUtil() {
    }

    public static String format(List<ObjectBuilder> children, BuildType buildType, String name, FormattingContext ctx) {
        if (children.isEmpty()) {
            return "";
        }
        boolean isMap = buildType == BuildType.MAP || (buildType == BuildType.METHOD && "put".equals(name));
        boolean forceMultiline = isMap && children.size() > 2;
        if (children.size() > ctx.getLineBreakCount() || forceMultiline) {
            boolean isMethodLevel = buildType == BuildType.METHOD;
            String prefixTabs = isMethodLevel ? "\t\t\t\t" : "\t\t\t";
            String suffixTabs = isMethodLevel ? "\t\t" : "\t";
            if (isMap) {
                return formatMapArguments(children, prefixTabs, suffixTabs);
            }
            return formatMultilineArguments(children, prefixTabs, suffixTabs);
        }
        return children.stream()
                .map(ArgumentFormatterUtil::getChildArgument)
                .collect(joining(ARGUMENT_DELIMITER));
    }

    private static String formatMapArguments(List<ObjectBuilder> children, String prefixTabs, String suffixTabs) {
        String prefix = System.lineSeparator() + prefixTabs;
        String suffix = System.lineSeparator() + suffixTabs;
        String pairDelimiter = "," + System.lineSeparator() + prefixTabs;

        return IntStream.iterate(0, i -> i < children.size(), i -> i + 2)
                .mapToObj(i -> {
                    if (i + 1 < children.size()) {
                        return getChildArgument(children.get(i)) + ", " + getChildArgument(children.get(i + 1));
                    }
                    return getChildArgument(children.get(i));
                })
                .collect(joining(pairDelimiter, prefix, suffix));
    }

    private static String formatMultilineArguments(List<ObjectBuilder> children, String prefixTabs, String suffixTabs) {
        String delimiter = "," + System.lineSeparator() + prefixTabs;
        String prefix = System.lineSeparator() + prefixTabs;
        String suffix = System.lineSeparator() + suffixTabs;
        return children.stream()
                .map(ArgumentFormatterUtil::getChildArgument)
                .collect(joining(delimiter, prefix, suffix));
    }

    static String getChildArgument(ObjectBuilder child) {
        if (child.isNullValue()) {
            return NULL;
        }
        if (isBasicValue(child)) {
            return child.getValue() == null ? NULL : child.getValue();
        }
        return child.getName();
    }
}
