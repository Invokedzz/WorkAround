package org.api.workaround.model;

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

    private final String value;

    DigitalInformation(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
