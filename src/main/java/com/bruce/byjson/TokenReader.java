package com.bruce.byjson;

/**
 * Created by bruceyuan on 17-5-19.
 */
public class TokenReader {
    private CharReader chReader;

    public TokenReader(CharReader chReader) {
        this.chReader = chReader;
    }

//    START_OBJ,
//    END_OBJ,
//    START_ARRAY,
//    END_ARRAY,
//    NULL,
//    STRING,
//    NUMBER,
//    BOOLEAN,
//    COMMA,
//    COLON,
//    END_DOC

    Token nextToken() {
        Token token = null;
        char ch = '?';
        do{
            if (!chReader.hasMore()) {
                return Token.END_DOC;
            }
            ch = chReader.peek();
        }while (isSpaceChar(ch));

        switch (ch){
            case '{' :
                chReader.skip();
                token = Token.START_OBJ;
                break;
            case '}' :
                chReader.skip();
                token = Token.END_OBJ;
                break;
            case '[' :
                chReader.skip();
                token = Token.START_ARRAY;
                break;
            case ']' :
                chReader.skip();
                token = Token.END_ARRAY;
                break;
            case ':' :
                chReader.skip();
                token = Token.COLON;
                break;
            case ',' :
                chReader.skip();
                token = Token.COMMA;
                break;
            case 'n' :
                token = Token.NULL;
                break;
            case 't' :
            case 'f' :
                token = Token.BOOLEAN;
                break;
            case '\"':
                chReader.skip();
                token = Token.STRING;
                break;
            default:
        }

        if (ch == '-' || isDigit(ch)){
            token = Token.NUMBER;
        }
        if (token == null){
            throw new JsonParseException("Unexpected char: " + ch, chReader.getReaded());
        }
        return token;
    }

    boolean readBoolean(){
        char c = chReader.next();
        String expected;
        if (c == 't'){
            expected = "ure";
        }else if (c == 'f'){
            expected = "alse";
        }else {
            throw new JsonParseException("Unexpected char: " + c, chReader.getReaded());
        }

        checkExpected(expected);

        return c == 't';
    }

    void readNull() {
        checkExpected("null");
    }

//    string = quotation-mark *char quotation-mark
//    char = unescaped /
//    escape (
//       %x22 /          ; "    quotation mark  U+0022
//            %x5C /          ; \    reverse solidus U+005C
//            %x2F /          ; /    solidus         U+002F
//            %x62 /          ; b    backspace       U+0008
//            %x66 /          ; f    form feed       U+000C
//            %x6E /          ; n    line feed       U+000A
//            %x72 /          ; r    carriage return U+000D
//            %x74 /          ; t    tab             U+0009
//            %x75 4HEXDIG )  ; uXXXX                U+XXXX
//    escape = %x5C          ; \
//    quotation-mark = %x22  ; "
//    unescaped = %x20-21 / %x23-5B / %x5D-10FFFF

    String readString(){
        StringBuilder builder = new StringBuilder();
        while (true){
            char c1 = chReader.next();
            if (c1 == '\\'){
                char c2 = chReader.next();
                switch (c2){
                    case '"':
                        builder.append("\"");
                        break;
                    case '\\':
                        builder.append("\\");
                        break;
                    case '/':
                        builder.append("/");
                        break;
                    case 'b':
                        builder.append("\b");
                        break;
                    case 'f':
                        builder.append("\f");
                        break;
                    case 'n':
                        builder.append("\n");
                        break;
                    case 'r':
                        builder.append("\r");
                        break;
                    case 't':
                        builder.append("\t");
                        break;
                    case 'u':
                        int u = 0;
                        for (int i = 0; i < 4; i++) {
                            char uch = chReader.next();
                            if (uch >= '0' && uch <= '9') {
                                u = (u << 4) + (uch - '0');
                            } else if (uch >= 'a' && uch <= 'f') {
                                u = (u << 4) + (uch - 'a') + 10;
                            } else if (uch >= 'A' && uch <= 'F') {
                                u = (u << 4) + (uch - 'A') + 10;
                            } else {
                                throw new JsonParseException("Unexpected char: " + uch,
                                        chReader.getReaded());
                            }
                        }
                        builder.append((char) u);
                        break;
                    default:
                        throw new JsonParseException("Unexpected char: " + c2, chReader.getReaded());
                }
            } else if(c1 == '\"'){
                break;
            } else if(c1 == '\n' || c1 == '\r'){
                throw new JsonParseException("Unexpected newline", chReader.getReaded());
            } else {
                builder.append(c1);
            }
        }
        return builder.toString();
    }

    //        number = [ "-" ] int [ frac ] [ exp ]
//        int = "0" / digit1-9 *digit
//        frac = "." 1*digit
//        exp = ("e" / "E") ["-" / "+"] 1*digit
    public Number readNumber(){
        //Number n = null;
        StringBuilder sbInt = null;
        StringBuilder sbFrac = null;
        StringBuilder sbExp = null;
        boolean isMinus = false;
        boolean isZeroInt = false;
        boolean isExpMinus = false;

        if (chReader.peek() == '-') {
            isMinus = true;
            chReader.skip();
        }

        char c = chReader.next();
        if (isDigit(c)){
            if (c == '0'){
                isZeroInt = true;
            }
            sbInt = new StringBuilder();
            sbInt.append(c);
        } else {
            throw new JsonParseException("Unexpected char: " + c, chReader.getReaded());
        }
        if (chReader.hasMore()) {
            c = chReader.peek();
            if (isZeroInt && c != '.' && isExp(c)) {
                throw new JsonParseException("Unexpected char: " + c, chReader.getReaded());
            }

            NumberReadingState state = NumberReadingState.INT;
            while (state != NumberReadingState.END) {
                if (chReader.hasMore()) {
                    c = chReader.peek();
                } else {
                    state = NumberReadingState.END;
                    break;
                }
                switch (state) {
                    case INT:
                        if (isDigit(c)) {
                            sbInt.append(chReader.next());
                        } else if (c == '.') {
                            chReader.skip();
                            sbFrac = new StringBuilder();
                            state = NumberReadingState.FRAC;
                        } else if (isExp(c)) {
                            chReader.skip();
                            isExpMinus = checkSignForExp();
                            sbExp = new StringBuilder();
                            state = NumberReadingState.EXP;
                        } else {
                            state = NumberReadingState.END;
                        }
                        break;
                    case FRAC:
                        if (isDigit(c)) {
                            sbFrac.append(chReader.next());
                        } else if (isExp(c)) {
                            chReader.skip();
                            isExpMinus = checkSignForExp();
                            sbExp = new StringBuilder();
                            state = NumberReadingState.EXP;
                        } else {
                            state = NumberReadingState.END;
                        }
                        break;
                    case EXP:
                        if (isDigit(c)) {
                            sbExp.append(chReader.next());
                        } else {
                            state = NumberReadingState.END;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        //Integer.parseInt(sbInt.toString());
        long ipart = stringToLong(sbInt);

        if (ipart < 0) {
            throw new JsonValueInvalidException(sbInt.toString() + " exceeded maximum value.", chReader.getReaded());
        }

        if (sbFrac == null && sbExp == null){
            return new Long(isMinus ? -ipart : ipart);
        }

        double result = ipart;
        if (sbFrac != null)
        {
            if (sbFrac.length() == 0) {
                throw new JsonParseException("Unexpected char: " + c, chReader.getReaded());
            }

            result += stringToFraction(sbFrac);
        }


        if (sbExp != null)
        {
            if (sbExp.length() == 0) {
                throw new JsonParseException("Unexpected char: " + c, chReader.getReaded());
            }
            long epart = stringToLong(sbExp);

            result = result * Math.pow(10, isExpMinus ? -epart: epart);
        }

        return new Double(isMinus ? -result : result);
    }

    enum NumberReadingState{
        INT,
        FRAC,
        EXP,
        END;
    }

    ///    \" \\ \/ \b \f \n \r \t
    private boolean isSpaceChar(char c) {
        boolean result = false;
        if (c == '\n' || c == '\r' || c == '\t' || c == ' '){
            result = true;
        }
        return result;
    }

    private void checkExpected(String expected) {
//        char[] chars = expected.toCharArray();
//        for (char item : chars) {
//            char a =chReader.next();
//            if (item != a){
//                throw new JsonParseException("Unexpected char: " + a, chReader.getReaded());
//            }
//        }
        for (int i = 0; i < expected.length(); i++){
            char item = chReader.next();
            if (item != expected.charAt(i)){
                throw new JsonParseException("Unexpected char: " + item, chReader.getReaded());
            }
        }
    }

    private boolean isDigit(char c){
        //Character.isDigit(ch)
        return c >= '0' && c <= '9';
    }

    private boolean isExp(char c){
        return c == 'e' || c == 'E';
    }

    private boolean checkSignForExp() {
        char sign = chReader.peek();
        if (sign == '+') {
            chReader.skip();
            return false;
        } else if (isDigit(sign)) {
            return false;
        } else if (sign == '-') {
            chReader.skip();
            return true;
        } else {
            throw new JsonParseException("Unexpected char: " + sign, chReader.getReaded());
        }
    }

    private long stringToLong(CharSequence sb) {
        long num = 0;

        for (int i = 0; i < sb.length(); i++) {
            num = num * 10 + (sb.charAt(i) - '0');
        }

        return num;
    }

    private double stringToFraction (CharSequence sb) {
        return stringToLong(sb) / Math.pow(10,sb.length());
    }

}
