package org.api.workaround.model.enums;

/**
 * Ways the attacker can encode the path in order to use Path Transversal
 */
public enum PathEncode {
    /**
     * Double URL encoded ".", value: %252e
     */
    DOUBLE_ENCODED_DOT("%252e", null, null),

    /**
     * Double URL encoded "/", value: %252f
     */
    DOUBLE_ENCODED_SLASH("%252f", null, null),

    /**
     * Double URL encoded "\", value: %255c
     */
    DOUBLE_ENCODED_BACKSLASH("%255c", null, null),

    /**
     * Unicode encoded ".", value: %u002e
     */
    UNICODE_DOT("%u002e", null, null),

    /**
     * Unicode encoded "/", value: %u2215
     */
    UNICODE_SLASH("%u2215", null, null),

    /**
     * Unicode encoded "\", value: %u2216
     */
    UNICODE_BACKSLASH("%u2216", null, null),

    /**
     * UTF-8 unicode encoded ".", values: %c0%2e, %e0%40%ae, %c0%ae
     */
    UTF8_UNICODE_DOT("%c0%2e", "%e0%40%ae", "%c0%ae"),

    /**
     * UTF-8 unicode encoded "/", values: %c0%af, %e0%80%af, %c0%2f
     */
    UTF8_UNICODE_SLASH("%c0%af", "%e0%80%af", "%c0%2f"),

    /**
     * UTF-8 unicode encoded "\", values: %c0%5c, %c0%80%5c
     */
    UTF8_UNICODE_BACKSLASH("%c0%5c", "%c0%80%5c", null);

    private final String value;
    private final String alterValue;
    private final String otherAlterValue;

    PathEncode(final String value, final String alterValue, final String otherAlterValue) {
        this.value = value;
        this.alterValue = alterValue;
        this.otherAlterValue = otherAlterValue;
    }

    /**
     * @return primary value as string
     */
    public String getValue() {
        return value;
    }

    /**
     * @return alternative value (option) as string, used in UTF-8 UNICODE
     */
    public String getAlterValue() {
        return alterValue;
    }

    /**
     * @return other alternative value (option 2) as string, used in UTF-8 UNICODE
     */
    public String getOtherAlterValue() {
        return otherAlterValue;
    }
}
