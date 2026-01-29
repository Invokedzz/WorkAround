package org.api.workaround.model;

public enum FileFormat {
    RAR(".rar"),
    CBR(".cbr"),
    PDF(".pdf"); // This shall be implemented soon

    private final String value;

    FileFormat(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
