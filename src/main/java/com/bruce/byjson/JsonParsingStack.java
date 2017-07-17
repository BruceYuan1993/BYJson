package com.bruce.byjson;

import java.util.List;
import java.util.Map;

/**
 * Created by bruceyuan on 17-6-17.
 */
public class JsonParsingStack extends Stack<ParsingValue> {
    public ParsingValue popWithCheck(ValueType type){
        ParsingValue value = pop();
        if (value.type != type) {
            throw new RuntimeException("Unexpected value type from stack.");
        }
        return  value;
    }

    public ParsingValue peekWithCheck(ValueType type){
        ParsingValue value = peek();
        if (value.type != type) {
            throw new RuntimeException("Unexpected value type from stack.");
        }
        return  value;
    }
}

class ParsingValue {

    Object value;
    ValueType type;

    public ParsingValue(ValueType type, Object value) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public ValueType getType() {
        return type;
    }

    public String asObjectKeyType(){
        return String.valueOf(value);
    }

    public Map<String, Object> asObjectType(){
        return (Map<String, Object>) value;
    }

    public List<Object> asArrayType(){
        return (List<Object>) value;
    }

    static ParsingValue newObjectValue (Object value) {
        return new ParsingValue(ValueType.OBJECT, value);
    }

    static ParsingValue newObjectKeyValue (Object value) {
        return new ParsingValue(ValueType.OBJECT_KEY, value);
    }

    static ParsingValue newArrayValue (Object value) {
        return new ParsingValue(ValueType.ARRAY, value);
    }
}

enum ValueType {
    OBJECT,
    OBJECT_KEY,
    ARRAY
}
