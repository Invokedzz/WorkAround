package org.api.workaround.model.enums;

public enum PathEncode {
    DOUBLE_ENCODED_DOT("%252e", null, null),
    DOUBLE_ENCODED_SLASH("%252f", null, null),
    DOUBLE_ENCODED_BACKSLASH("%255c", null, null),
    UNICODE_DOT("%u002e", null, null),
    UNICODE_SLASH("%u2215", null, null),
    UNICODE_BACKSLASH("%u2216", null, null),
    UTF8_UNICODE_DOT("%c0%2e", "%e0%40%ae", "%c0%ae"),
    UTF8_UNICODE_SLASH("%c0%af", "%e0%80%af", "%c0%2f"),
    UTF8_UNICODE_BACKSLASH("%c0%5c", "%c0%80%5c", null);

    private final String value;
    private final String alterValue;
    private final String otherAlterValue;

    PathEncode(final String value, final String alterValue, final String otherAlterValue) {
        this.value = value;
        this.alterValue = alterValue;
        this.otherAlterValue = otherAlterValue;
    }

    public String getValue() {
        return value;
    }

    public String getAlterValue() {
        return alterValue;
    }

    public String getOtherAlterValue() {
        return otherAlterValue;
    }
}
