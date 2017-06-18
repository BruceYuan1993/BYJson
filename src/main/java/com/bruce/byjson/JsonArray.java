package com.bruce.byjson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by bruceyuan on 17-6-18.
 */
public class JsonArray implements Json{
    public List<Object> elements;

    public JsonArray(List<Object> elements) {
        this.elements = elements;
    }

    public JsonArray() {
        this.elements = new ArrayList<>();
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    @Override
    public int size() {
        return elements.size();
    }

    public void add(Object value){
        elements.add(value);
    }

    public void add(int index, Object value){
        elements.add(index, value);
    }
    public void addAll(Collection collection) {
        elements.addAll(collection);
    }

    public void addAll(int index, Collection collection) {
        elements.addAll(index, collection);
    }

    public Object get(int index) {
        return elements.get(index);
    }

    public boolean getBoolean(int index) {
        Object o = elements.get(index);
        return (boolean) o;
    }
    public double getDouble(int index){
        return (double)((long) elements.get(index));
    }
    public int getInt(int index){
        return (int)((long) elements.get(index));
    }
    public long getLong(int index) {
        return (long) elements.get(index);
    }
    public JsonObject getJsonObject(int index) {
        Object o = elements.get(index);
        if (o instanceof JsonObject){
            return (JsonObject) o;
        }
        else if (o instanceof Map){
            return new JsonObject((Map<String,Object>) o);
        }else{
            throw new ClassCastException("Not JsonArray");
        }
    }
    public JsonArray getJsonArray(int index) {
        Object o = elements.get(index);
        if (o instanceof JsonArray){
            return (JsonArray) o;
        }
        else if (o instanceof List){
            return new JsonArray((List<Object>) o);
        }else{
            throw new ClassCastException("Not JsonArray");
        }
    }
    public String getString(int index) {
        return (String) elements.get(index);
    }
}
