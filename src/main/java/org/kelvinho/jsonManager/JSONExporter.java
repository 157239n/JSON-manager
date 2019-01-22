package org.kelvinho.jsonManager;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class JSONExporter {
    private String file = null;

    private class NoFileFoundException extends RuntimeException {
        NoFileFoundException() {
            super();
        }
    }

    public JSONExporter() {
    }

    public JSONExporter(@Nonnull final String fileToWriteTo) {
        this.file = fileToWriteTo;
    }

    public abstract String generateJSON(@Nonnull final StringBuilder stringBuilder);

    public String generateJSON() {
        return generateJSON(new StringBuilder());
    }

    public void exportJSON(@Nonnull final String fileToWriteTo) {
        this.file = fileToWriteTo;
        exportJSON();
    }

    public void exportJSON() {
        if (file == null) {
            throw new NoFileFoundException();
        }
        Environment.IO.writeText(file, generateJSON());
    }
}
