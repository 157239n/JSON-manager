package org.kelvinho.jsonManager;

import org.json.JSONException;
import org.json.JSONObject;
import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class JSONImporter {
    private String file = null;

    private class NoFileFoundException extends RuntimeException {
        NoFileFoundException() {
            super();
        }
    }

    public class ParsingWrongObjectTypeException extends RuntimeException {
        public ParsingWrongObjectTypeException() {
            super();
        }
    }

    public class CannotParseException extends RuntimeException {
        CannotParseException() {
            super();
        }
    }

    public JSONImporter() {
    }

    public JSONImporter(@Nonnull final String fileToReadFrom) {
        this.file = fileToReadFrom;
    }

    public Object generateObject(@Nonnull final String JSONText) {
        try {
            return generateObject(new JSONObject(JSONText));
        } catch (JSONException e) {
            e.printStackTrace();
            return new Object();
        }
    }

    public abstract Object generateObject(@Nonnull final JSONObject jsonObject);

    public Object importJSON(@Nonnull final String fileToReadFrom) {
        this.file = fileToReadFrom;
        return importJSON();
    }

    public Object importJSON() {
        if (file == null) {
            throw new NoFileFoundException();
        }
        return generateObject(Environment.IO.readText(file));
    }

}
