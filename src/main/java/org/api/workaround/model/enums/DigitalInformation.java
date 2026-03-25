package org.api.workaround.model.enums;

/**
 * Units of digital information.
 */
public enum DigitalInformation {
    /**
     * KB unit (kb)
     */
    KB("kb"),

    /**
     * MB unit (mb)
     */
    MB("mb"),

    /**
     * GB unit (gb)
     */
    GB("gb"),

    /**
     * BYTES unit (bytes)
     */
    BYTES("bytes");

    public static class StandardFileProperties {
        public final static String MAX_FILES_AVAILABLE = "8";
    }

    private final String value;

    DigitalInformation(final String value) {
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
