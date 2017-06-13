package com.bruce.byjson;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * Created by bruceyuan on 17-5-10.
 */
public class CharReader {
    static final String REACHED_END = "Reached end";
    private static final int BUFF_SIZE = 1024;
    private char[] buff;
    private Reader reader;
    private int pos;
    private int size;
    private int readed;

    public CharReader(Reader reader) {
        this.reader = reader;
        buff=new char[BUFF_SIZE];
    }

    public char next(){
        checkAndRefreshBuff(REACHED_END);
        char c = buff[pos];
        pos++;
        return c;
    }

    public String next(int len){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<len; i++){
            builder.append(next());
        }
        return builder.toString();
    }

    public char peek(){
        checkAndRefreshBuff(REACHED_END);
        char c =  buff[pos];
        return c;
    }

    public boolean hasMore(){
        checkAndRefreshBuff(null);
        return pos < size;
    }

    public void skip(){
        checkAndRefreshBuff(REACHED_END);
        pos++;
    }

    public int getReaded() {
        return readed;
    }

    private void checkAndRefreshBuff(String exceptionMsg) {
        if (pos == size){
            refreshBuff(exceptionMsg);
        }
    }

    private void refreshBuff(String exceptionMsg) {
        try {
            int len = reader.read(buff);

            if (len == -1){
                if (exceptionMsg == null || exceptionMsg.equals("")){
                    return;
                }else{
                    throw new JsonParseException(exceptionMsg, readed);
                }
            }

            size = len;
            readed += len;
            pos = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
