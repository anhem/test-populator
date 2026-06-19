package com.github.anhem.testpopulator.internal.object;

@SuppressWarnings("unchecked")
public abstract class BaseBuilder<B extends BaseBuilder<B>> {
    protected Class<?> clazz;
    protected String name;
    protected BuildType buildType;
    protected FormattingContext formattingContext = new FormattingContext(false, 3);
    protected int expectedChildren;
    protected boolean parameterized;
    protected Class<?>[] referencedClasses = new Class<?>[0];

    public B clazz(Class<?> clazz) {
        this.clazz = clazz;
        return (B) this;
    }

    public B name(String name) {
        this.name = name;
        return (B) this;
    }

    public B buildType(BuildType buildType) {
        this.buildType = buildType;
        return (B) this;
    }

    public B formattingContext(FormattingContext formattingContext) {
        this.formattingContext = formattingContext;
        return (B) this;
    }

    public B expectedChildren(int expectedChildren) {
        this.expectedChildren = expectedChildren;
        return (B) this;
    }

    public B parameterized(boolean parameterized) {
        this.parameterized = parameterized;
        return (B) this;
    }

    public B referencedClasses(Class<?>... referencedClasses) {
        this.referencedClasses = referencedClasses;
        return (B) this;
    }

    public abstract ObjectBuilder build();
}
