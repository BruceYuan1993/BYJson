package com.bruce.byjson.practice;

/**
 * Created by bruceyuan on 17-5-4.
 */
public enum PTokenType {
    START_OBJ,
    END_OBJ,
    START_ARRAY,
    END_ARRAY,
    NULL,
    STRING,
    NUMBER,
    BOOLEAN,
    COMMA,
    COLON,
    END_DOC
}
