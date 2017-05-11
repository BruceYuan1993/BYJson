package com.bruce.byjson;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by bruceyuan on 17-5-10.
 */
public class CharReader {
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
        checkAndRefreshBuff();
        char c =  buff[pos];
        pos++;
        return c;
    }

    public boolean hasMore(){
        checkAndRefreshBuff();
        return pos < size;
    }

    public String next(int len){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<len; i++){
            builder.append(next());
        }
        return builder.toString();
    }

    public char peek(){
        checkAndRefreshBuff();
        char c =  buff[pos];
        return c;
    }


    private void checkAndRefreshBuff() {
        if (pos == size){
            refreshBuff();
        }
    }

    private void refreshBuff() {
        refreshBuff(false);
    }

    private void refreshBuff(boolean throwException) {
        try {
            int len = reader.read(buff);
            if (len == -1){
                if (throwException){
                    throw new RuntimeException();
                }else{
                    return;
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
