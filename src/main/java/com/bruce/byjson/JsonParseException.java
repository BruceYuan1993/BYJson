package com.bruce.byjson;

/**
 * Created by bruceyuan on 17-6-5.
 */
public class JsonParseException extends JsonException{
    private final int errorIndex;

    public JsonParseException(String message, int errorIndex) {
        super(message);
        this.errorIndex = errorIndex;
    }

    public JsonParseException(String message) {
        this(message,-1);
    }

    public int getErrorIndex() {
        return errorIndex;
    }
}
