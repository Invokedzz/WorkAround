package org.api.workaround.model;

public enum FileFormat {
    RAR(".rar"),
    CBR(".cbr");

    private final String value;

    FileFormat(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
