package com.bruce.byjson;

import java.util.EnumSet;

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
    private EnumSet<JsonReadingState> status = null;

    public void parse(){
        status = EnumSet.of(JsonReadingState.ARRAY_START,
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
                    //status.contains()
                    break;
                case OBJ_END:
                    break;
                case ARRAY_START:
                    break;
                case ARRAY_END:
                    break;
                case NULL:
                    if (status.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (status.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected null", reader.getReader().getReaded());
                    //break;
                case STRING:
                    if (status.contains(JsonReadingState.PAIR_KEY)) {}
                    if (status.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (status.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected boolean", reader.getReader().getReaded());
                    //break;
                case BOOLEAN:
                    if (status.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (status.contains(JsonReadingState.PAIR_VALUE)) {}
                    throw new JsonParseException("Unexpected boolean", reader.getReader().getReaded());
                    //break;
                case NUMBER:
                    if (status.contains(JsonReadingState.ARRAY_ITEM)) {}
                    if (status.contains(JsonReadingState.PAIR_VALUE)) {}
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
