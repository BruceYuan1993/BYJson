package com.bruce.byjson;

import java.util.List;
import java.util.Map;

/**
 * Created by bruceyuan on 17-6-18.
 */
public class ByJson {
    public static Json parse(String s) {
        JsonReader reader = new JsonReader();
        Object o = reader.parse(s);
        if (o instanceof List){
            return new JsonArray((List<Object>) o);
        }
        if (o instanceof Map){
            return new JsonObject((Map<String,Object>) o);
        }
        return null;
    }
}
