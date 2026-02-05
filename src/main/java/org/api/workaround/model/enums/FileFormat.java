package org.api.workaround.model.enums;

/**
 * Available file formats.
 */
public enum FileFormat {
    /**
     * RAR format (.rar)
     */
    RAR(".rar"),

    /**
     * CBR format (.cbr)
     */
    CBR(".cbr"),

    /**
     * PDF format (.pdf)
     */
    PDF(".pdf"); // This shall be implemented soon

    private final String value;

    FileFormat(final String value) {
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
