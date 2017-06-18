package com.bruce.byjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruceyuan on 17-6-18.
 */
public class JsonObject implements Json{
    Map<String,Object> elements;

    public JsonObject(Map<String,Object> elements) {
        this.elements = elements;
    }

    public JsonObject() {
        this.elements = new HashMap<>();
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    @Override
    public int size() {
        return elements.size();
    }

    public Object get(String key) {
        return elements.get(key);
    }

    public boolean getBoolean(String key) {
        Object o = elements.get(key);
        return (boolean) o;
    }
    public double getDouble(String key){
        return (double)((long) elements.get(key));
    }
    public int getInt(String key){
        return (int) elements.get(key);
    }
    public long getLong(String key) {
        return (long) elements.get(key);
    }
    public JsonObject getJsonObject(String key) {
        Object o = elements.get(key);
        if (o instanceof JsonObject){
            return (JsonObject) o;
        }
        else if (o instanceof Map){
            return new JsonObject((Map<String,Object>) o);
        }else{
            throw new ClassCastException("Not JsonObject");
        }
    }
    public JsonArray getJsonArray(String key) {
        Object o = elements.get(key);
        if (o instanceof JsonArray){
            return (JsonArray) o;
        }
        else if (o instanceof List){
            return new JsonArray((List<Object>) o);
        }else{
            throw new ClassCastException("Not JsonArray");
        }
    }
    public String getString(String key) {
        return (String) elements.get(key);
    }
}
