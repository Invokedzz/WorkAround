package org.api.workaround.model.enums;

/**
 * Punctuations to work with directories and files. Or just to remove the need of hardcoded strings.
 */
public enum Punctuation {
    /**
     * Slash (/)
     */
    SLASH("/"),

    /**
     * Apostrophe (,)
     */
    APOSTROPHE(",");

    public static class Literal {
        public static final String TRUE_LITERAL = "true";
        public static final String FALSE_LITERAL = "false";
    }

    private final String value;

    Punctuation(final String value) {
        this.value = value;
    }

    /**
     * @return the enum name as a string
     */
    @Override
    public String toString() {
        return value;
    }
}
