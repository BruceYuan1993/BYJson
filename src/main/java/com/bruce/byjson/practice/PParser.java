package com.bruce.byjson.practice;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * object = {} | { members }
 members = pair | pair , members
 pair = string : value
 array = [] | [ elements ]
 elements = value  | value , elements
 value = string | number | object | array | true | false | null


 */
public class PParser {
    private PTokenizer tokenizer;

    public PParser(PTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private PJson json(){
        PTokenType tokenType = tokenizer.peek(0).getType();
        if (tokenType == PTokenType.START_ARRAY){
            return array();
        }else if (tokenType == PTokenType.START_OBJ){
            return object();
        }else{
            throw new PBYJsonException("Invalid JSON input.");
        }
    }

    private PObject object() {
        tokenizer.next();
        Map<String, PValue> map = new HashMap<String, PValue>();
        if (isToken(PTokenType.STRING)){
            map = key(map);
        }else if (isToken(PTokenType.END_OBJ)) {
            tokenizer.next(); //consume '}'
            return new PObject(map);
        }
        return new PObject(map);
    }


    private Map<String,PValue> key(Map<String, PValue> map) {
        String key = tokenizer.next().getValue();
        if (!isToken(PTokenType.COLON)){
            throw new PBYJsonException("Invalid JSON input.");
        }
        tokenizer.next();
        if (isPrimary()){
            PPrimary primary = new PPrimary(tokenizer.next().getValue());
            map.put(key,primary);
        }else if (isToken(PTokenType.START_OBJ)){
            PObject obj = object();
            map .put(key, obj);
        }else if (isToken(PTokenType.START_ARRAY)){
            PValue array = array();
            map.put(key, array);
        }

        if (isToken(PTokenType.COMMA)) {
            tokenizer.next(); //consume ','
            if (isToken(PTokenType.STRING)) {
                map = key(map);
            }
        } else if (isToken(PTokenType.END_OBJ)) {
            tokenizer.next(); //consume '}'
            return map;
        } else {
            throw new PBYJsonException("Invalid JSON input.");
        }
        return map;
    }

    private PArray array() {
        tokenizer.next();
        List<PJson> list = new ArrayList<PJson>();
        PArray array = null;

        if (isPrimary()){
            element(list);
        }else if (isToken(PTokenType.START_OBJ)){
            list.add(object());
            while (isToken(PTokenType.COMMA)) {
                tokenizer.next(); //consume ','
                list.add(object());
            }
        }else if(isToken(PTokenType.END_ARRAY)){
            tokenizer.next();
            array =  new PArray(list);
            return array;
        }else if (isToken(PTokenType.START_ARRAY)){
            array = array();
            list.add(array);
            /*
            if (isToken(PTokenType.COMMA)) {
                tokenizer.next(); //consume ','
                list = element(list);
            }
            */
            while (isToken(PTokenType.COMMA)) {
                tokenizer.next(); //consume ','
                list = element(list);
            }
        }
        tokenizer.next();
        array =  new PArray(list);
        return array;
    }

    private List<PJson> element(List<PJson> list) {
        list.add(new PPrimary(tokenizer.next().getValue()));
        if (isToken(PTokenType.COMMA)){
            tokenizer.next();
            if (isPrimary()){
                list= element(list);
            }else if(isToken(PTokenType.START_OBJ)){
                list.add(object());
            }else if(isToken(PTokenType.START_ARRAY)){
                list.add(array());
            }else {
                throw new PBYJsonException("Invalid JSON input.");
            }
        }else if (isToken(PTokenType.END_ARRAY)){
            return list;
        }else {
            throw new PBYJsonException("Invalid JSON input.");
        }
        return list;
    }

    private boolean isToken(PTokenType type){
        PToken t = tokenizer.peek(0);
        return t.getType() == type;
    }


    private  boolean isPrimary(){
        PToken t = tokenizer.peek(0);
        return t.getType() == PTokenType.BOOLEAN || t.getType() == PTokenType.NULL ||
                t.getType() == PTokenType.NUMBER || t.getType() == PTokenType.STRING;
    }

    public static PObject parseJSONObject(String s) throws Exception {
        PTokenizer tokenizer = new PTokenizer(new BufferedReader(new StringReader(s)));
        tokenizer.tokenizer();
        PParser parser = new PParser(tokenizer);
        return parser.object();
    }

    public static PArray parseJSONArray(String s) throws Exception {
        PTokenizer tokenizer = new PTokenizer(new BufferedReader(new StringReader(s)));
        tokenizer.tokenizer();
        PParser parser = new PParser(tokenizer);
        return parser.array();
    }

}
