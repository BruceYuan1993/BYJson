package com.bruce.byjson.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruceyuan on 17-5-8.
 */
public class PArray implements PJson, PValue {
    private List<PJson> list = new ArrayList<PJson>();

    public PArray(List<PJson> list) {
        this.list = list;
    }

    public int length() {
        return list.size();
    }

    public void add(PJson element) {
        list.add(element);
    }

    public PJson get(int i) {
        return list.get(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i =0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            if (i != list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    public Object value() {
        return this;
    }
}
