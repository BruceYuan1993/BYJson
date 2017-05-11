package com.bruce.byjson.practice;

/**
 * Created by bruceyuan on 17-5-4.
 */
public class PToken {
    private PTokenType type;
    private String value;

    public PToken(PTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public PTokenType getType() {

        return type;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
