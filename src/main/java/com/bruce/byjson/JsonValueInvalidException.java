package com.bruce.byjson;

/**
 * Created by bruceyuan on 17-6-10.
 */
public class JsonValueInvalidException extends JsonParseException{
    public JsonValueInvalidException(String message, int errorIndex) {
        super(message, errorIndex);
    }
}
