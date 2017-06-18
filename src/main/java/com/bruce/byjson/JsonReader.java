package com.bruce.byjson;

import java.io.StringReader;
import java.util.*;

/**primitive
 * Created by bruceyuan on 17-6-13.
 */
public class JsonReader {
    enum JsonReadingState {
        ARRAY_START,
        ARRAY_END,
        ARRAY_ITEM_PRIMITIVE,
        OBJ_START,
        OBJ_END,
        PAIR_KEY,
        PAIR_VALUE_PRIMITIVE,
        COMMA,
        COLON,
        DOC_END;
    }

    private TokenReader reader = null;
    private EnumSet<JsonReadingState> expectStatus = null;

    public Object parse(String s){
        reader = new TokenReader(new CharReader(new StringReader(s)));
        JsonParsingStack stack = new JsonParsingStack();
        expectStatus = EnumSet.of(JsonReadingState.ARRAY_START,
                            JsonReadingState.OBJ_START);

        while (true){
            Token token = reader.nextToken();
            switch (token) {
                case OBJ_START:
                    if (expectStatus.contains(JsonReadingState.OBJ_START)) {
                        stack.push(ParsingValue.newObjectValue(new HashMap<String,Object>()));
                        expectStatus = EnumSet.of(JsonReadingState.PAIR_KEY,
                                            JsonReadingState.OBJ_END);
                        continue;
                    }
                    throw new JsonParseException("Unexpected {", reader.getReader().getReaded());
                case OBJ_END:
                    if (expectStatus.contains(JsonReadingState.OBJ_END)) {
                        handleEndToken(stack, ValueType.OBJECT);
                        continue;
                    }
                    throw new JsonParseException("Unexpected }.", reader.getReader().getReaded());
                case ARRAY_START:
                    if (expectStatus.contains(JsonReadingState.ARRAY_START)) {
                        stack.push(ParsingValue.newArrayValue(new LinkedList<Object>()));
                        expectStatus = EnumSet.of(JsonReadingState.ARRAY_END,
                                                JsonReadingState.ARRAY_ITEM_PRIMITIVE,
                                                JsonReadingState.ARRAY_START,
                                                JsonReadingState.OBJ_START);
                        continue;
                    }
                    throw new JsonParseException("Unexpected [", reader.getReader().getReaded());
                case ARRAY_END:
                    if (expectStatus.contains(JsonReadingState.ARRAY_END)) {
                        handleEndToken(stack, ValueType.ARRAY);
                        continue;
                    }
                    throw new JsonParseException("Unexpected ].", reader.getReader().getReaded());
                case NULL:
                    reader.readNull();
                    handlePrimitive(stack, null);
                    continue;
                case STRING:
                    String str = reader.readString();
                    if (expectStatus.contains(JsonReadingState.PAIR_KEY)) {
                        stack.push(ParsingValue.newObjectKeyValue(str));
                        expectStatus = EnumSet.of(JsonReadingState.COLON);
                        continue;
                    }
                    handlePrimitive(stack, s);
                    continue;
                case BOOLEAN:
                    boolean b = reader.readBoolean();
                    handlePrimitive(stack, b);
                    continue;
                case NUMBER:
                    Number n = reader.readNumber();
                    handlePrimitive(stack, n);
                    continue;
                case COMMA:
                    if (expectStatus.contains(JsonReadingState.COMMA)) {
                        ValueType preType = stack.peek().getType();
                        if (preType == ValueType.OBJECT) {
                            expectStatus = EnumSet.of(JsonReadingState.PAIR_KEY);
                        }

                        if (preType == ValueType.ARRAY) {
                            expectStatus = EnumSet.of(
                                    JsonReadingState.ARRAY_ITEM_PRIMITIVE,
                                    JsonReadingState.ARRAY_START,
                                    JsonReadingState.OBJ_START);
                        }
                    }
                    break;
                case COLON:
                    if (expectStatus.contains(JsonReadingState.COLON)) {
                        expectStatus = EnumSet.of(JsonReadingState.PAIR_VALUE_PRIMITIVE,JsonReadingState.ARRAY_START,JsonReadingState.OBJ_START);
                        continue;
                    }
                    throw new JsonParseException("Unexpected :", reader.getReader().getReaded());
                case DOC_END:
                    if (expectStatus.contains(JsonReadingState.DOC_END)) {
                        ParsingValue value = stack.pop();
                        if (stack.empty()) {
                            return value.getValue();
                        }
                    }
                    throw new JsonParseException("Unexpected EOF.", reader.getReader().getReaded());
            }
        }
    }

    private void handlePrimitive(JsonParsingStack stack, Object value) {
        try {
            if (expectStatus.contains(JsonReadingState.ARRAY_ITEM_PRIMITIVE)) {
                addItemForArray(stack, value);
                expectStatus = EnumSet.of(JsonReadingState.COMMA, JsonReadingState.ARRAY_END);
                return;
            }
            if (expectStatus.contains(JsonReadingState.PAIR_VALUE_PRIMITIVE)) {
                addPairForObject(stack,value);
                expectStatus = EnumSet.of(JsonReadingState.COMMA, JsonReadingState.OBJ_END);
                return;
            }
            throw new JsonParseException("Unexpected " + value == null ? "null" : value.toString(), reader.getReader().getReaded());
            //break;
        } catch (Exception e) {
            throw new JsonParseException("Pasre error at " + reader.getReader().getReaded());
        }
    }

    private void handleEndToken(JsonParsingStack stack, ValueType type) {
        try {
            ParsingValue item = stack.popWithCheck(type);
            if (stack.empty()) {
                stack.push(item);
                expectStatus = EnumSet.of(JsonReadingState.DOC_END);
                return;
            }

            ValueType preType = stack.peek().getType();
            if (preType == ValueType.OBJECT_KEY) {
                addPairForObject(stack, item.value);
                expectStatus = EnumSet.of(JsonReadingState.COMMA,
                        JsonReadingState.OBJ_END);
                return;
            }

            if (preType == ValueType.ARRAY) {
                addItemForArray(stack,item.value);
                expectStatus = EnumSet.of(JsonReadingState.COMMA,
                        JsonReadingState.ARRAY_END);
                return;
            }
            throw new JsonParseException("Unexpected " + item.getValue(),
                        reader.getReader().getReaded());
        } catch (Exception e) {
            throw new JsonParseException("Pasre error at " + reader.getReader().getReaded());
        }
    }

    private void addPairForObject(JsonParsingStack stack, Object value) {
        String key = stack.pop().asObjectKeyType();
        stack.peekWithCheck(ValueType.OBJECT).asObjectType().
                put(key, value);
    }

    private void addItemForArray(JsonParsingStack stack, Object value) {
        stack.peekWithCheck(ValueType.ARRAY).asArrayType().add(value);
    }
}
