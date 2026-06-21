package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.isBasicValue;

public class ArgumentFormatterUtil {

    public static final String ARGUMENT_DELIMITER = ", ";
    private static final String NULL = ObjectBuilder.NULL;

    private static final int MAX_LINE_LENGTH = 100;

    private ArgumentFormatterUtil() {
    }

    public static String format(List<ObjectBuilder> children, BuildType buildType, String name, boolean useFullyQualifiedName) {
        if (children.isEmpty()) {
            return "";
        }
        
        List<String> arguments = children.stream()
                .map(ArgumentFormatterUtil::getChildArgument)
                .collect(Collectors.toList());

        boolean isMap = buildType == BuildType.MAP || (buildType == BuildType.METHOD && "put".equals(name));
        boolean forceMultiline = isMap && children.size() > 2;

        int totalLength = arguments.stream().mapToInt(String::length).sum() + Math.max(0, arguments.size() - 1) * ARGUMENT_DELIMITER.length();

        if (totalLength > MAX_LINE_LENGTH || forceMultiline) {
            boolean isMethodLevel = buildType == BuildType.METHOD;
            String prefixTabs = isMethodLevel ? "\t\t\t\t" : "\t\t\t";
            String suffixTabs = isMethodLevel ? "\t\t" : "\t";
            if (isMap) {
                return formatMapArguments(arguments, prefixTabs, suffixTabs);
            }
            return formatMultilineArguments(arguments, prefixTabs, suffixTabs);
        }
        return String.join(ARGUMENT_DELIMITER, arguments);
    }

    private static String formatMapArguments(List<String> arguments, String prefixTabs, String suffixTabs) {
        String prefix = System.lineSeparator() + prefixTabs;
        String suffix = System.lineSeparator() + suffixTabs;
        String pairDelimiter = "," + System.lineSeparator() + prefixTabs;

        return IntStream.iterate(0, i -> i < arguments.size(), i -> i + 2)
                .mapToObj(i -> {
                    if (i + 1 < arguments.size()) {
                        return arguments.get(i) + ", " + arguments.get(i + 1);
                    }
                    return arguments.get(i);
                })
                .collect(Collectors.joining(pairDelimiter, prefix, suffix));
    }

    private static String formatMultilineArguments(List<String> arguments, String prefixTabs, String suffixTabs) {
        String delimiter = "," + System.lineSeparator() + prefixTabs;
        String prefix = System.lineSeparator() + prefixTabs;
        String suffix = System.lineSeparator() + suffixTabs;
        return arguments.stream()
                .collect(Collectors.joining(delimiter, prefix, suffix));
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
