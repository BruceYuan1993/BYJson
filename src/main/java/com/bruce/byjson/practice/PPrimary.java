package com.bruce.byjson.practice;

/**
 * Created by bruceyuan on 17-5-9.
 */
public class PPrimary implements PJson,PValue {
    private String value;

    public PPrimary(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public Object value() {
        return value;
    }
}
