package com.bruce.byjson.practice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruceyuan on 17-5-8.
 */
public class PObject implements PJson,PValue {
    private Map<String, PValue> map = new HashMap<String, PValue>();

    public PObject(Map<String, PValue> map) {
        this.map = map;
    }

    public int getInt(String key) {
        return Integer.parseInt((String) map.get(key).value());
    }

    public String getString(String key) {
        return (String) map.get(key).value();
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean((String) map.get(key).value());
    }

    public PArray getJArray(String key) {
        return (PArray) map.get(key).value();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        int size = map.size();
        for (String key : map.keySet()) {
            sb.append(key + " : " + map.get(key));
            if (--size != 0) {
                sb.append(", ");
            }
        }
        sb.append(" }");
        return sb.toString();
    }

    public Object value(){
        return map;
    }
}
