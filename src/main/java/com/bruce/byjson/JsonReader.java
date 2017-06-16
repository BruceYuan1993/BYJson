package com.bruce.byjson;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by bruceyuan on 17-6-13.
 */
public class JsonReader {
    enum JsonReadingState {
        ARRAY_START,
        ARRAY_END,
        ARRAY_ITEM,
        OBJ_START,
        OBJ_END,
        PAIR_KEY,
        PAIR_VALUE,
        COMMA,
        COLON,
        DOC_END;
    }

    private TokenReader reader = null;
    private EnumSet<JsonReadingState> expectStatus = null;

    public void parse(){
        Stack stack = new Stack();
        expectStatus = EnumSet.of(JsonReadingState.ARRAY_START,
                JsonReadingState.OBJ_START);

        while (true){
//            OBJ_START,
//                    OBJ_END,
//                    ARRAY_START,
//                    ARRAY_END,
//                    NULL,
//                    STRING,
//                    NUMBER,
//                    BOOLEAN,
//                    COMMA,
//                    COLON,
//                    DOC_END
            Token token = reader.nextToken();
            switch (token) {
                case OBJ_START:
                    if (expectStatus.contains(JsonReadingState.OBJ_START)) {
                        stack.push(new HashMap<String,Object>());
                        expectStatus = EnumSet.of(JsonReadingState.PAIR_KEY,
                                JsonReadingState.OBJ_END);
                    }
                    continue;
                case OBJ_END:
                    if (expectStatus.contains(JsonReadingState.OBJ_END)) {
                        stack.push(new HashMap<String,Object>());
                        expectStatus = EnumSet.of(JsonReadingState.DOC_END,
                                JsonReadingState.COMMA, JsonReadingState.OBJ_END,
                                JsonReadingState.ARRAY_END);
                    }
                    break;
                case ARRAY_START:
                    break;
                case ARRAY_END:
                    break;
                case NULL:
                    if (expectStatus.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (expectStatus.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected null", reader.getReader().getReaded());
                    //break;
                case STRING:
                    if (expectStatus.contains(JsonReadingState.PAIR_KEY)) {}
                    if (expectStatus.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (expectStatus.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected boolean", reader.getReader().getReaded());
                    //break;
                case BOOLEAN:
                    if (expectStatus.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (expectStatus.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected boolean", reader.getReader().getReaded());
                    //break;
                case NUMBER:
                    if (expectStatus.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (expectStatus.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected null", reader.getReader().getReaded());
                    //break;
                case COMMA:
                    break;
                case COLON:
                    break;
                case DOC_END:
                    break;

            }
        }
    }
}
