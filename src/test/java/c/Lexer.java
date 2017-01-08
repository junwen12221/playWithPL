//package c;
//
//import sun.nio.cs.ArrayDecoder;
//
//import java.nio.charset.CharsetDecoder;
//import java.nio.charset.StandardCharsets;
//import java.util.Queue;
//
//import static c.CharTypes.*;
//import static c.Token.*;
//
//
///**
// * Created by Administrator on 2016/12/28 0028.
// */
//public class Lexer {
//    char[] text = new char[8192];
//    int len;
//    char ch;
//    protected int pos;
//
//    public Lexer(byte[] input, int offset, int len) {
//        CharsetDecoder cd = StandardCharsets.UTF_8.newDecoder();
//        //   int en = scale(input.length, cd.maxCharsPerByte());
//        ArrayDecoder ad = (ArrayDecoder) cd;
//        len = ad.decode(input, offset, len, text);
//    }
//
//    public Token token() {
//        return token;
//    }
//    protected int          bufPos;
//    public final void nextToken() {
//        bufPos = 0;
//        if (comments != null && comments.size() > 0) {
//            comments = null;
//        }
//
//        this.lines = 0;
//        int startLine = line;
//
//        for (;;) {
//            if (isWhitespace(ch)) {
//                if (ch == '\n') {
//                    line++;
//
//                    lines = line - startLine;
//                }
//
//                scanChar();
//                continue;
//            }
//
//            if (ch == '$' && charAt(pos + 1) == '{') {
//                scanVariable();
//                return;
//            }
//
//            if (isFirstIdentifierChar(ch)) {
//                if (ch == 'N') {
//                    if (charAt(pos + 1) == '\'') {
//                        ++pos;
//                        ch = '\'';
//                        scanString();
//                        token = Token.LITERAL_NCHARS;
//                        return;
//                    }
//                }
//
//                scanIdentifier();
//                return;
//            }
//
//            switch (ch) {
//                case '0':
//                    if (charAt(pos + 1) == 'x') {
//                        scanChar();
//                        scanChar();
//                        scanHexaDecimal();
//                    } else {
//                        scanNumber();
//                    }
//                    return;
//                case '1':
//                case '2':
//                case '3':
//                case '4':
//                case '5':
//                case '6':
//                case '7':
//                case '8':
//                case '9':
//                    scanNumber();
//                    return;
//                case ',':
//                case '，':
//                    scanChar();
//                    token = COMMA;
//                    return;
//                case '(':
//                    scanChar();
//                    token = LPAREN;
//                    return;
//                case ')':
//                    scanChar();
//                    token = RPAREN;
//                    return;
//                case '[':
//                    scanLBracket();
//                    return;
//                case ']':
//                    scanChar();
//                    token = RBRACKET;
//                    return;
//                case '{':
//                    scanChar();
//                    token = LBRACE;
//                    return;
//                case '}':
//                    scanChar();
//                    token = RBRACE;
//                    return;
//                case ':':
//                    scanChar();
//                    if (ch == '=') {
//                        scanChar();
//                        token = COLONEQ;
//                    } else if (ch == ':') {
//                        scanChar();
//                        token = COLONCOLON;
//                    } else {
//                        unscan();
//                        scanVariable();
//                    }
//                    return;
//                case '#':
//                    scanSharp();
//                    if ((token() == Token.LINE_COMMENT || token() == Token.MULTI_LINE_COMMENT) && skipComment) {
//                        bufPos = 0;
//                        continue;
//                    }
//                    return;
//                case '.':
//                    scanChar();
//                    if (isDigit(ch) && !isFirstIdentifierChar(charAt(pos - 2))) {
//                        unscan();
//                        scanNumber();
//                        return;
//                    } else if (ch == '.') {
//                        scanChar();
//                        if (ch == '.') {
//                            scanChar();
//                            token = Token.DOTDOTDOT;
//                        } else {
//                            token = Token.DOTDOT;
//                        }
//                    } else {
//                        token = Token.DOT;
//                    }
//                    return;
//                case '\'':
//                    scanString();
//                    return;
//                case '\"':
//                    scanAlias();
//                    return;
//                case '*':
//                    scanChar();
//                    token = Token.STAR;
//                    return;
//                case '?':
//                    scanChar();
//                    token = Token.QUES;
//                    return;
//                case ';':
//                    scanChar();
//                    token = Token.SEMI;
//                    return;
//                case '`':
//                    throw new ParserException("TODO"); // TODO
//                case '@':
//                    scanVariable();
//                    return;
//                case '-':
//                    if (charAt(pos +1) == '-') {
//                        scanComment();
//                        if ((token() == Token.LINE_COMMENT || token() == Token.MULTI_LINE_COMMENT) && skipComment) {
//                            bufPos = 0;
//                            continue;
//                        }
//                    } else {
//                        scanOperator();
//                    }
//                    return;
//                case '/':
//                    int nextChar = charAt(pos + 1);
//                    if (nextChar == '/' || nextChar == '*') {
//                        scanComment();
//                        if ((token() == Token.LINE_COMMENT || token() == Token.MULTI_LINE_COMMENT) && skipComment) {
//                            bufPos = 0;
//                            continue;
//                        }
//                    } else {
//                        token = Token.SLASH;
//                        scanChar();
//                    }
//                    return;
//                default:
//                    if (Character.isLetter(ch)) {
//                        scanIdentifier();
//                        return;
//                    }
//
//                    if (isOperator(ch)) {
//                        scanOperator();
//                        return;
//                    }
//
//                    // QS_TODO ?
//                    if (isEOF()) { // JLS
//                        token = EOF;
//                    } else {
//                        lexError("illegal.char", String.valueOf((int) ch));
//                        scanChar();
//                    }
//
//                    return;
//            }
//        }
//
//    }
//    private final void scanOperator() {
//        switch (ch) {
//            case '+':
//                scanChar();
//                token = Token.PLUS;
//                break;
//            case '-':
//                scanChar();
//                if (ch == '>') {
//                    scanChar();
//                    if (ch == '>') {
//                        scanChar();
//                        token = Token.SUBGTGT;
//                    } else {
//                        token = Token.SUBGT;
//                    }
//                } else {
//                    token = Token.SUB;
//                }
//                break;
//            case '*':
//                scanChar();
//                token = Token.STAR;
//                break;
//            case '/':
//                scanChar();
//                token = Token.SLASH;
//                break;
//            case '&':
//                scanChar();
//                if (ch == '&') {
//                    scanChar();
//                    token = Token.AMPAMP;
//                } else {
//                    token = Token.AMP;
//                }
//                break;
//            case '|':
//                scanChar();
//                if (ch == '|') {
//                    scanChar();
//                    if (ch == '/') {
//                        scanChar();
//                        token = Token.BARBARSLASH;
//                    } else {
//                        token = Token.BARBAR;
//                    }
//                } else if (ch == '/') {
//                    scanChar();
//                    token = Token.BARSLASH;
//                } else {
//                    token = Token.BAR;
//                }
//                break;
//            case '^':
//                scanChar();
//                token = Token.CARET;
//                break;
//            case '%':
//                scanChar();
//                token = Token.PERCENT;
//                break;
//            case '=':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.EQEQ;
//                } else {
//                    token = Token.EQ;
//                }
//                break;
//            case '>':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.GTEQ;
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.GTGT;
//                } else {
//                    token = Token.GT;
//                }
//                break;
//            case '<':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    if (ch == '>') {
//                        token = Token.LTEQGT;
//                        scanChar();
//                    } else {
//                        token = Token.LTEQ;
//                    }
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.LTGT;
//                } else if (ch == '<') {
//                    scanChar();
//                    token = Token.LTLT;
//                } else if (ch == '@') {
//                    scanChar();
//                    token = Token.LT_MONKEYS_AT;
//                } else {
//                    token = Token.LT;
//                }
//                break;
//            case '!':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.BANGEQ;
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.BANGGT;
//                } else if (ch == '<') {
//                    scanChar();
//                    token = Token.BANGLT;
//                } else if (ch == '!') {
//                    scanChar();
//                    token = Token.BANGBANG; // postsql
//                } else if (ch == '~') {
//                    scanChar();
//                    if (ch == '*') {
//                        scanChar();
//                        token = Token.BANG_TILDE_STAR; // postsql
//                    } else {
//                        token = Token.BANG_TILDE; // postsql
//                    }
//                } else {
//                    token = Token.BANG;
//                }
//                break;
//            case '?':
//                scanChar();
//                token = Token.QUES;
//                break;
//            case '~':
//                scanChar();
//                if (ch == '*') {
//                    scanChar();
//                    token = Token.TILDE_STAR;
//                } else if (ch == '=') {
//                    scanChar();
//                    token = Token.TILDE_EQ; // postsql
//                } else {
//                    token = Token.TILDE;
//                }
//                break;
//            default:
//                throw new ParserException("TODO");
//        }
//    }
//
//    protected void scanString() {
//        mark = pos;
//        boolean hasSpecial = false;
//
//        for (;;) {
//            if (isEOF()) {
//                lexError("unclosed.str.lit");
//                return;
//            }
//
//            ch = charAt(++pos);
//
//            if (ch == '\'') {
//                scanChar();
//                if (ch != '\'') {
//                    token = LITERAL_CHARS;
//                    break;
//                } else {
//                    if (!hasSpecial) {
//                        initBuff(bufPos);
//                        arraycopy(mark + 1, buf, 0, bufPos);
//                        hasSpecial = true;
//                    }
//                    putChar('\'');
//                    continue;
//                }
//            }
//
//            if (!hasSpecial) {
//                bufPos++;
//                continue;
//            }
//
//            if (bufPos == buf.length) {
//                putChar(ch);
//            } else {
//                buf[bufPos++] = ch;
//            }
//        }
//
//        if (!hasSpecial) {
//            stringVal = subString(mark + 1, bufPos);
//        } else {
//            stringVal = new String(buf, 0, bufPos);
//        }
//    }
//
//    protected final void scanString2() {
//        {
//            boolean hasSpecial = false;
//            int startIndex = pos + 1;
//            int endIndex = -1; // text.indexOf('\'', startIndex);
//            for (int i = startIndex; i < text.length(); ++i) {
//                final char ch = text.charAt(i);
//                if (ch == '\\') {
//                    hasSpecial = true;
//                    continue;
//                }
//                if (ch == '\'') {
//                    endIndex = i;
//                    break;
//                }
//            }
//
//            if (endIndex == -1) {
//                throw new ParserException("unclosed str");
//            }
//
//            String stringVal = subString(startIndex, endIndex - startIndex);
//            // hasSpecial = stringVal.indexOf('\\') != -1;
//
//            if (!hasSpecial) {
//                this.stringVal = stringVal;
//                int pos = endIndex + 1;
//                char ch = charAt(pos);
//                if (ch != '\'') {
//                    this.pos = pos;
//                    this.ch = ch;
//                    token = LITERAL_CHARS;
//                    return;
//                }
//            }
//        }
//
//        mark = pos;
//        boolean hasSpecial = false;
//        for (;;) {
//            if (isEOF()) {
//                lexError("unclosed.str.lit");
//                return;
//            }
//
//            ch = charAt(++pos);
//
//            if (ch == '\\') {
//                scanChar();
//                if (!hasSpecial) {
//                    initBuff(bufPos);
//                    arraycopy(mark + 1, buf, 0, bufPos);
//                    hasSpecial = true;
//                }
//
//                switch (ch) {
//                    case '0':
//                        putChar('\0');
//                        break;
//                    case '\'':
//                        putChar('\'');
//                        break;
//                    case '"':
//                        putChar('"');
//                        break;
//                    case 'b':
//                        putChar('\b');
//                        break;
//                    case 'n':
//                        putChar('\n');
//                        break;
//                    case 'r':
//                        putChar('\r');
//                        break;
//                    case 't':
//                        putChar('\t');
//                        break;
//                    case '\\':
//                        putChar('\\');
//                        break;
//                    case 'Z':
//                        putChar((char) 0x1A); // ctrl + Z
//                        break;
//                    default:
//                        putChar(ch);
//                        break;
//                }
//
//                continue;
//            }
//            if (ch == '\'') {
//                scanChar();
//                if (ch != '\'') {
//                    token = LITERAL_CHARS;
//                    break;
//                } else {
//                    if (!hasSpecial) {
//                        initBuff(bufPos);
//                        arraycopy(mark + 1, buf, 0, bufPos);
//                        hasSpecial = true;
//                    }
//                    putChar('\'');
//                    continue;
//                }
//            }
//
//            if (!hasSpecial) {
//                bufPos++;
//                continue;
//            }
//
//            if (bufPos == buf.length) {
//                putChar(ch);
//            } else {
//                buf[bufPos++] = ch;
//            }
//        }
//
//        if (!hasSpecial) {
//            stringVal = subString(mark + 1, bufPos);
//        } else {
//            stringVal = new String(buf, 0, bufPos);
//        }
//    }
//
//    protected void scanAlias() {
//        mark = pos;
//
//        if (buf == null) {
//            buf = new char[32];
//        }
//
//        boolean hasSpecial = false;
//        for (;;) {
//            if (isEOF()) {
//                lexError("unclosed.str.lit");
//                return;
//            }
//
//            ch = charAt(++pos);
//
//            if (ch == '\"' && charAt(pos - 1) != '\\') {
//                scanChar();
//                token = LITERAL_ALIAS;
//                break;
//            }
//
//            if(ch == '\\') {
//                scanChar();
//                if (ch == '"') {
//                    hasSpecial = true;
//                } else {
//                    unscan();
//                }
//            }
//
//            if (bufPos == buf.length) {
//                putChar(ch);
//            } else {
//                buf[bufPos++] = ch;
//            }
//        }
//
//        if (!hasSpecial) {
//            stringVal = subString(mark + 1, bufPos);
//        } else {
//            stringVal = new String(buf, 0, bufPos);
//        }
//
//        //stringVal = subString(mark + 1, bufPos);
//    }
//
//    protected final void scanAlias2() {
//        {
//            boolean hasSpecial = false;
//            int startIndex = pos + 1;
//            int endIndex = -1; // text.indexOf('\'', startIndex);
//            for (int i = startIndex; i < text.length(); ++i) {
//                final char ch = text.charAt(i);
//                if (ch == '\\') {
//                    hasSpecial = true;
//                    continue;
//                }
//                if (ch == '"') {
//                    endIndex = i;
//                    break;
//                }
//            }
//
//            if (endIndex == -1) {
//                throw new ParserException("unclosed str");
//            }
//
//            String stringVal = subString(startIndex, endIndex - startIndex);
//            // hasSpecial = stringVal.indexOf('\\') != -1;
//
//            if (!hasSpecial) {
//                this.stringVal = stringVal;
//                int pos = endIndex + 1;
//                char ch = charAt(pos);
//                if (ch != '\'') {
//                    this.pos = pos;
//                    this.ch = ch;
//                    token = LITERAL_CHARS;
//                    return;
//                }
//            }
//        }
//
//        mark = pos;
//        boolean hasSpecial = false;
//        for (;;) {
//            if (isEOF()) {
//                lexError("unclosed.str.lit");
//                return;
//            }
//
//            ch = charAt(++pos);
//
//            if (ch == '\\') {
//                scanChar();
//                if (!hasSpecial) {
//                    initBuff(bufPos);
//                    arraycopy(mark + 1, buf, 0, bufPos);
//                    hasSpecial = true;
//                }
//
//                switch (ch) {
//                    case '0':
//                        putChar('\0');
//                        break;
//                    case '\'':
//                        putChar('\'');
//                        break;
//                    case '"':
//                        putChar('"');
//                        break;
//                    case 'b':
//                        putChar('\b');
//                        break;
//                    case 'n':
//                        putChar('\n');
//                        break;
//                    case 'r':
//                        putChar('\r');
//                        break;
//                    case 't':
//                        putChar('\t');
//                        break;
//                    case '\\':
//                        putChar('\\');
//                        break;
//                    case 'Z':
//                        putChar((char) 0x1A); // ctrl + Z
//                        break;
//                    default:
//                        putChar(ch);
//                        break;
//                }
//
//                continue;
//            }
//            if (ch == '\"') {
//                scanChar();
//                token = LITERAL_CHARS;
//                break;
//            }
//
//            if (!hasSpecial) {
//                bufPos++;
//                continue;
//            }
//
//            if (bufPos == buf.length) {
//                putChar(ch);
//            } else {
//                buf[bufPos++] = ch;
//            }
//        }
//
//        if (!hasSpecial) {
//            stringVal = subString(mark + 1, bufPos);
//        } else {
//            stringVal = new String(buf, 0, bufPos);
//        }
//    }
//
//    public void scanSharp() {
//        scanVariable();
//    }
//
//    public void scanVariable() {
//        if (ch != '@' && ch != ':' && ch != '#' && ch != '$') {
//            throw new ParserException("illegal variable");
//        }
//
//        mark = pos;
//        bufPos = 1;
//        char ch;
//
//        final char c1 = charAt(pos + 1);
//        if (c1 == '@') {
//            if (JdbcConstants.POSTGRESQL.equalsIgnoreCase(dbType)) {
//                pos += 2;
//                token = Token.MONKEYS_AT_AT;
//                this.ch = charAt(++pos);
//                return;
//            }
//            ch = charAt(++pos);
//
//            bufPos++;
//        } else if (c1 == '>' && JdbcConstants.POSTGRESQL.equalsIgnoreCase(dbType)) {
//            pos += 2;
//            token = Token.MONKEYS_AT_GT;
//            this.ch = charAt(++pos);
//            return;
//        } else if (c1 == '{') {
//            pos++;
//            bufPos++;
//
//            for (;;) {
//                ch = charAt(++pos);
//
//                if (ch == '}') {
//                    break;
//                }
//
//                bufPos++;
//                continue;
//            }
//
//            if (ch != '}') {
//                throw new ParserException("syntax error");
//            }
//            ++pos;
//            bufPos++;
//
//            this.ch = charAt(pos);
//
//            stringVal = addSymbol();
//            token = Token.VARIANT;
//            return;
//        }
//
//        for (;;) {
//            ch = charAt(++pos);
//
//            if (!isIdentifierChar(ch)) {
//                break;
//            }
//
//            bufPos++;
//            continue;
//        }
//
//        this.ch = charAt(pos);
//
//        stringVal = addSymbol();
//        token = Token.VARIANT;
//    }
//
//    public void scanComment() {
//        if (!allowComment) {
//            throw new NotAllowCommentException();
//        }
//
//        if ((ch == '/' && charAt(pos + 1) == '/')
//                || (ch == '-' && charAt(pos + 1) == '-')) {
//            scanSingleLineComment();
//        } else if (ch == '/' && charAt(pos + 1) == '*') {
//            scanMultiLineComment();
//        } else {
//            throw new IllegalStateException();
//        }
//    }
//
//    private void scanMultiLineComment() {
//        Token lastToken = this.token;
//        Queue
//        scanChar();
//        scanChar();
//        mark = pos;
//        bufPos = 0;
//
//        for (;;) {
//            if (ch == '*' && charAt(pos + 1) == '/') {
//                scanChar();
//                scanChar();
//                break;
//            }
//
//            // multiline comment结束符错误
//            if (ch == EOI) {
//                throw new ParserException("unterminated /* comment.");
//            }
//            scanChar();
//            bufPos++;
//        }
//
//        stringVal = subString(mark, bufPos);
//        token = Token.MULTI_LINE_COMMENT;
//        commentCount++;
//        if (keepComments) {
//            addComment(stringVal);
//        }
//
//        if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
//            return;
//        }
//
//        if (!isAllowComment() && !isSafeComment(stringVal)) {
//            throw new NotAllowCommentException();
//        }
//    }
//
//    private void scanSingleLineComment() {
//        Token lastToken = this.token;
//
//        scanChar();
//        scanChar();
//        mark = pos;
//        bufPos = 0;
//
//        for (;;) {
//            if (ch == '\r') {
//                if (charAt(pos + 1) == '\n') {
//                    line++;
//                    scanChar();
//                    break;
//                }
//                bufPos++;
//                break;
//            }
//
//            if (ch == '\n') {
//                line++;
//                scanChar();
//                break;
//            }
//
//            // single line comment结束符错误
//            if (ch == EOI) {
//                throw new ParserException("syntax error at end of input.");
//            }
//
//            scanChar();
//            bufPos++;
//        }
//
//        stringVal = subString(mark, bufPos);
//        token = Token.LINE_COMMENT;
//        commentCount++;
//        if (keepComments) {
//            addComment(stringVal);
//        }
//
//        if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
//            return;
//        }
//
//        if (!isAllowComment() && !isSafeComment(stringVal)) {
//            throw new NotAllowCommentException();
//        }
//    }
//
//    public void scanIdentifier() {
//        final char first = ch;
//
//        final boolean firstFlag = isFirstIdentifierChar(first);
//        if (!firstFlag) {
//            throw new ParserException("illegal identifier");
//        }
//
//        mark = pos;
//        bufPos = 1;
//        char ch;
//        for (;;) {
//            ch = charAt(++pos);
//
//            if (!isIdentifierChar(ch)) {
//                break;
//            }
//
//            bufPos++;
//            continue;
//        }
//
//        this.ch = charAt(pos);
//
//        stringVal = addSymbol();
//        Token tok = keywods.getKeyword(stringVal);
//        if (tok != null) {
//            token = tok;
//        } else {
//            token = Token.IDENTIFIER;
//        }
//    }
//
//    public void scanNumber() {
//        mark = pos;
//
//        if (ch == '-') {
//            bufPos++;
//            ch = charAt(++pos);
//        }
//
//        for (;;) {
//            if (ch >= '0' && ch <= '9') {
//                bufPos++;
//            } else {
//                break;
//            }
//            ch = charAt(++pos);
//        }
//
//        boolean isDouble = false;
//
//        if (ch == '.') {
//            if (charAt(pos + 1) == '.') {
//                token = Token.LITERAL_INT;
//                return;
//            }
//            bufPos++;
//            ch = charAt(++pos);
//            isDouble = true;
//
//            for (;;) {
//                if (ch >= '0' && ch <= '9') {
//                    bufPos++;
//                } else {
//                    break;
//                }
//                ch = charAt(++pos);
//            }
//        }
//
//        if (ch == 'e' || ch == 'E') {
//            bufPos++;
//            ch = charAt(++pos);
//
//            if (ch == '+' || ch == '-') {
//                bufPos++;
//                ch = charAt(++pos);
//            }
//
//            for (;;) {
//                if (ch >= '0' && ch <= '9') {
//                    bufPos++;
//                } else {
//                    break;
//                }
//                ch = charAt(++pos);
//            }
//
//            isDouble = true;
//        }
//
//        if (isDouble) {
//            token = Token.LITERAL_FLOAT;
//        } else {
//            token = Token.LITERAL_INT;
//        }
//    }
//
//    public void scanHexaDecimal() {
//        mark = pos;
//
//        if (ch == '-') {
//            bufPos++;
//            ch = charAt(++pos);
//        }
//
//        for (;;) {
//            if (CharTypes.isHex(ch)) {
//                bufPos++;
//            } else {
//                break;
//            }
//            ch = charAt(++pos);
//        }
//
//        token = Token.LITERAL_HEX;
//    }
//
//    public String hexString() {
//        return subString(mark, bufPos);
//    }
//
//    public final boolean isDigit(char ch) {
//        return ch >= '0' && ch <= '9';
//    }
//
//    /**
//     * Append a character to sbuf.
//     */
//    protected final void putChar(char ch) {
//        if (bufPos == buf.length) {
//            char[] newsbuf = new char[buf.length * 2];
//            System.arraycopy(buf, 0, newsbuf, 0, buf.length);
//            buf = newsbuf;
//        }
//        buf[bufPos++] = ch;
//    }
//    protected void unscan() {
//        ch = charAt(--pos);
//    }
//    public final char charAt(int index) {
//        if (index >= text.length) {
//            return EOI;
//        }
//
//        return text[index];
//    }
//
//    protected final void scanChar() {
//        ch = charAt(++pos);
//    }
//
//    private final void scanOperator() {
//        switch (ch) {
//            case '+':
//                scanChar();
//                token = Token.PLUS;
//                break;
//            case '-':
//                scanChar();
//                if (ch == '>') {
//                    scanChar();
//                    if (ch == '>') {
//                        scanChar();
//                        token = Token.SUBGTGT;
//                    } else {
//                        token = Token.SUBGT;
//                    }
//                } else {
//                    token = Token.SUB;
//                }
//                break;
//            case '*':
//                scanChar();
//                token = Token.STAR;
//                break;
//            case '/':
//                scanChar();
//                token = Token.SLASH;
//                break;
//            case '&':
//                scanChar();
//                if (ch == '&') {
//                    scanChar();
//                    token = Token.AMPAMP;
//                } else {
//                    token = Token.AMP;
//                }
//                break;
//            case '|':
//                scanChar();
//                if (ch == '|') {
//                    scanChar();
//                    if (ch == '/') {
//                        scanChar();
//                        token = Token.BARBARSLASH;
//                    } else {
//                        token = Token.BARBAR;
//                    }
//                } else if (ch == '/') {
//                    scanChar();
//                    token = Token.BARSLASH;
//                } else {
//                    token = Token.BAR;
//                }
//                break;
//            case '^':
//                scanChar();
//                token = Token.CARET;
//                break;
//            case '%':
//                scanChar();
//                token = Token.PERCENT;
//                break;
//            case '=':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.EQEQ;
//                } else {
//                    token = Token.EQ;
//                }
//                break;
//            case '>':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.GTEQ;
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.GTGT;
//                } else {
//                    token = Token.GT;
//                }
//                break;
//            case '<':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    if (ch == '>') {
//                        token = Token.LTEQGT;
//                        scanChar();
//                    } else {
//                        token = Token.LTEQ;
//                    }
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.LTGT;
//                } else if (ch == '<') {
//                    scanChar();
//                    token = Token.LTLT;
//                } else if (ch == '@') {
//                    scanChar();
//                    token = Token.LT_MONKEYS_AT;
//                } else {
//                    token = Token.LT;
//                }
//                break;
//            case '!':
//                scanChar();
//                if (ch == '=') {
//                    scanChar();
//                    token = Token.BANGEQ;
//                } else if (ch == '>') {
//                    scanChar();
//                    token = Token.BANGGT;
//                } else if (ch == '<') {
//                    scanChar();
//                    token = Token.BANGLT;
//                } else if (ch == '!') {
//                    scanChar();
//                    token = Token.BANGBANG; // postsql
//                } else if (ch == '~') {
//                    scanChar();
//                    if (ch == '*') {
//                        scanChar();
//                        token = Token.BANG_TILDE_STAR; // postsql
//                    } else {
//                        token = Token.BANG_TILDE; // postsql
//                    }
//                } else {
//                    token = Token.BANG;
//                }
//                break;
//            case '?':
//                scanChar();
//                token = Token.QUES;
//                break;
//            case '~':
//                scanChar();
//                if (ch == '*') {
//                    scanChar();
//                    token = Token.TILDE_STAR;
//                } else if (ch == '=') {
//                    scanChar();
//                    token = Token.TILDE_EQ; // postsql
//                } else {
//                    token = Token.TILDE;
//                }
//                break;
//            default:
//                throw new ParserException("TODO");
//        }
//    }
//
//    public static void main(String[] args) {
//
//    }
//
//    final static byte EOI = 0x1A;
//
//
//    public Token token;
//
//    protected Keywords keywods = Keywords.DEFAULT_KEYWORDS;
//
//    private static int scale(int len, float expansionFactor) {
//        // We need to perform double, not float, arithmetic; otherwise
//        // we lose low order bits when len is larger than 2**24.
//        return (int) (len * (double) expansionFactor);
//    }
//}
