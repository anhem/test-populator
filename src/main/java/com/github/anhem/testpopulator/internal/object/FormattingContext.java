package com.github.anhem.testpopulator.internal.object;

public class FormattingContext {
    private final boolean useFullyQualifiedName;
    private final int lineBreakCount;

    public FormattingContext(boolean useFullyQualifiedName, int lineBreakCount) {
        this.useFullyQualifiedName = useFullyQualifiedName;
        this.lineBreakCount = lineBreakCount;
    }

    public boolean isUseFullyQualifiedName() {
        return useFullyQualifiedName;
    }

    public int getLineBreakCount() {
        return lineBreakCount;
    }
}
