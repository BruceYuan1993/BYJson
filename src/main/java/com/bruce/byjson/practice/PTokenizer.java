package com.bruce.byjson.practice;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class PTokenizer {
    private Reader reader;
    private int c;
    private boolean isUnread = false;
    private int savedChar;
    private List<PToken> tokens = new ArrayList<PToken>();

    public List<PToken> getTokens(){
        return tokens;
    }

    public void tokenizer() throws IOException {
        PToken token;
        do {
            token = readToken();
            tokens.add(token);
        }while (token.getType() != PTokenType.END_DOC);
    }

    public PTokenizer(Reader reader) {
        this.reader = reader;
    }

    private int readChar() throws IOException {
        if (!isUnread) {
            int c = reader.read();
            savedChar = c;
            return c;
        } else {
            isUnread = false;
            return savedChar;
        }
    }

    private PToken readToken() throws IOException {
        PToken token = null;
        do {
            c = readChar();
        }while (isSpace(c));


        if (isNull(c)){
            token = new PToken(PTokenType.NULL,null);
        }else if(isTrue(c)){
           token = new PToken(PTokenType.BOOLEAN,"true");
        }else if(isFalse(c)){
            token = new PToken(PTokenType.BOOLEAN,"false");
        }else if (c == ','){
            token = new PToken(PTokenType.COMMA, ",");
        }else if (c == ':'){
            token = new PToken(PTokenType.COLON, ":");
        }else if (c == '{'){
            token = new PToken(PTokenType.START_OBJ, "{");
        }else if (c == '}'){
            token = new PToken(PTokenType.END_OBJ, "}");
        }else if (c == '['){
            token = new PToken(PTokenType.START_ARRAY, "[");
        }else if (c == ']'){
            token = new PToken(PTokenType.END_ARRAY, "]");
        }else if (c == '"'){
            token = readString();
        }else if (isNum(c)){
            unread();
            return readNum();
        }
        else if (c == -1) {
            return new PToken(PTokenType.END_DOC, "EOF");
        } else {
            System.out.println((char) c);
            throw new PBYJsonException("Invalid JSON input.");
        }

        return token;
    }

    private PToken readNum() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c = readChar();
        if (c == '-') { //-
            sb.append((char) c);
            c = readChar();
            if (c == '0') { //-0
                sb.append((char) c);
                numAppend(sb);

            } else if (isDigitOne2Nine(c)) { //-digit1-9
                do {
                    sb.append((char) c);
                    c = readChar();
                } while (isDigit(c));
                unread();
                numAppend(sb);
            } else {
                throw new PBYJsonException("- not followed by digit");
            }
        } else if (c == '0') { //0
            sb.append((char) c);
            numAppend(sb);
        } else if (isDigitOne2Nine(c)) { //digit1-9
            do {
                sb.append((char) c);
                c = readChar();
            } while (isDigit(c));
            unread();
            numAppend(sb);
        }
        return new PToken(PTokenType.NUMBER, sb.toString()); //the value of 0 is null
    }

    private boolean isDigitOne2Nine(int c){
        return c >= '1' && c <= '9';
    }

    private void numAppend(StringBuilder sb) throws IOException {
        c = readChar();
        if (c == '.') { //int frac
            sb.append((char) c); //apppend '.'
            appendFrac(sb);
            if (isExp(c)) { //int frac exp
                sb.append((char) c); //append 'e' or 'E';
                appendExp(sb);
            }

        } else if (isExp(c)) { // int exp
            sb.append((char) c); //append 'e' or 'E'
            appendExp(sb);
        } else {
            unread();
        }
    }

    private boolean isExp(int c) throws IOException {
        return c == 'e' || c == 'E';
    }

    private void appendFrac(StringBuilder sb) throws IOException {
        c = readChar();
        while (isDigit(c)) {
            sb.append((char) c);
            c = readChar();
        }
    }

    private void appendExp(StringBuilder sb) throws IOException {
        int c = readChar();
        if (c == '+' || c == '-') {
            sb.append((char) c); //append '+' or '-'
            c = readChar();
            if (!isDigit(c)) {
                throw new PBYJsonException("e+(-) or E+(-) not followed by digit");
            } else { //e+(-) digit
                do {
                    sb.append((char) c);
                    c = readChar();
                } while (isDigit(c));
                unread();
            }
        } else if (!isDigit(c)) {
            throw new PBYJsonException("e or E not followed by + or - or digit.");
        } else { //e digit
            do {
                sb.append((char) c);
                c = readChar();
            } while (isDigit(c));
            unread();
        }
    }

    private void unread() {
        isUnread = true;
    }

    private PToken readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true){
            c = readChar();
            if (isEscape(c)){
                if (c == 'u'){
                    sb.append('\\').append((char)c);
                    for (int i = 0; i < 4; i++){
                        c = readChar();
                        if (isHex(c)){
                            sb.append((char)c);
                        }else {
                            throw new PBYJsonException("Invalid JSON input.");
                        }
                    }
                }else {
                    sb.append('\\').append((char)c);
                }
            }else if (c == '"'){
                return new PToken(PTokenType.STRING, sb.toString());
            }else if (c == '\r' || c == '\n') {
                throw new PBYJsonException("Invalid JSON input.");
            }else {
                sb.append((char) c);
            }
        }
    }

    private boolean isHex(int c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isNum(int c) {
        return isDigit(c) || c == '-';
    }

    private boolean isEscape(int c) throws IOException {
        if (c == '\\'){
            c = readChar();
            if (c == '"' || c == '\\' || c == '/' || c == 'b' ||
                    c == 'f' || c == 'n' || c == 't' || c == 'r' || c == 'u'){
                return true;
            }else {
                throw new PBYJsonException("Invalid JSON input.");
            }
        }else {
            return false;
        }
    }

    private boolean isSpace(int c) {
        return c >= 0 && c <= ' ';
    }

    private boolean isNull(int ch) throws IOException {
        boolean result = false;
        if (ch == 'n'){
            if (readChar() == 'u'){
                if (readChar() == 'l'){
                    if (readChar() == 'l'){
                        result = true;
                    }else {
                        throw new PBYJsonException("Invalid json input.");
                    }
                }else {
                    throw new PBYJsonException("Invalid json input.");
                }
            }else {
                throw new PBYJsonException("Invalid json input.");
            }
        }
        return result;
    }

    private boolean isTrue(int ch) throws IOException {
        boolean result = false;
        if (ch == 't'){
            if (readChar() == 'r'){
                if (readChar() == 'u'){
                    if (readChar() == 'e'){
                        result = true;
                    }else {
                        throw new PBYJsonException("Invalid json input.");
                    }
                }else {
                    throw new PBYJsonException("Invalid json input.");
                }
            }else {
                throw new PBYJsonException("Invalid json input.");
            }
        }
        return result;
    }
    private boolean isFalse(int ch) throws IOException {
        boolean result = false;
        if (ch == 'f'){
            if (readChar() == 'a'){
                if (readChar() == 'l'){
                    if (readChar() == 's'){
                        if (readChar() == 'e'){
                            result = true;
                        }else {
                            throw new PBYJsonException("Invalid json input.");
                        }
                    }else {
                        throw new PBYJsonException("Invalid json input.");
                    }
                }else {
                    throw new PBYJsonException("Invalid json input.");
                }
            }else {
                throw new PBYJsonException("Invalid json input.");
            }
        }
        return result;
    }

    public PToken next() {
        return tokens.remove(0);
    }

    public PToken peek(int i) {
        return tokens.get(i);
    }

    public boolean hasNext() {
        return tokens.get(0).getType() != PTokenType.END_DOC;
    }
}
