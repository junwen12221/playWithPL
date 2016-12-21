package com.alibaba.druid;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;

import static com.alibaba.druid.sql.parser.Token.*;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class Main {
    public static void main(String[] args) {
        MySqlLexer lexer = new MySqlLexer("select a from b");
        select_statement(lexer);
    }

    static void table_references(Lexer lexer) {
        table_reference(lexer);
        while (true) {
            if (lexer.token() == Token.COMMA) {
                table_reference(lexer);
            } else {
                break;
            }
        }
    }

    /**
     * table_reference:
     * table_factor1 | table_atom
     *
     * @param lexer
     */
    static void table_reference(Lexer lexer) {

    }

    static void table_factor1(Lexer lexer) {

    }

    static void table_factor2(Lexer lexer) {

    }

    static void table_factor3(Lexer lexer) {

    }

    static void table_factor4(Lexer lexer) {
        table_atom(lexer);
        if (identifierEquals(lexer, "NATURAL")) {

        }
    }

    static void table_atom(Lexer lexer) {
        {
            table_spec(lexer);
            partition_clause(lexer);
            as(lexer);
            index_hint_list(lexer);
        }
        {
            subquery(lexer);
            as(lexer);
        }
        {
            if (identifierEquals(lexer, "OJ")) {
                table_reference(lexer);
                accept(lexer, LEFT);
                accept(lexer, OUTER);
                accept(lexer, JOIN);
                table_reference(lexer);
                accept(lexer, ON);
                ExpMain.expression(lexer);
            }
        }
    }

    static void subquery(Lexer lexer) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() == SELECT) {
                select_statement(lexer);
            } else {
                table_reference(lexer);
            }
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
            }
        }
    }

    static void select_statement(Lexer lexer) {
        select_expression(lexer);
        while (lexer.token() == Token.UNION || lexer.token() == Token.ALL || lexer.token() == Token.SELECT) {
            if (lexer.token() == Token.UNION || lexer.token() == Token.ALL) {
                lexer.nextToken();
            }
            select_expression(lexer);
        }
    }

    static void select_expression(Lexer lexer) {
        accept(lexer, Token.SELECT);
        if (lexer.token() == Token.ALL || lexer.token() == Token.DISTINCT || identifierEquals(lexer, "DISTINCTROW")) {
            lexer.nextToken();
        }
        if (identifierEquals(lexer, "HIGH_PRIORITY")) {
            lexer.nextToken();
        }
        if (identifierEquals(lexer, "STRAIGHT_JOIN")) {
            lexer.nextToken();
        }
        if (identifierEquals(lexer, "SQL_SMALL_RESULT")) {
            lexer.nextToken();
        }

        if (identifierEquals(lexer, "SQL_BIG_RESULT")) {
            lexer.nextToken();
        }

        if (identifierEquals(lexer, "SQL_BUFFER_RESULT")) {
            lexer.nextToken();
        }

        if (identifierEquals(lexer, "SQL_CACHE")) {
            lexer.nextToken();
        }

        if (identifierEquals(lexer, "SQL_NO_CACHE")) {
            lexer.nextToken();
        }

        if (identifierEquals(lexer, "SQL_CALC_FOUND_ROWS")) {
            lexer.nextToken();
        }
        select_list(lexer);
        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            table_references(lexer);
            partition_clause(lexer);
            where_clause(lexer);
            orderby_clause(lexer);
            limit_clause(lexer);
            if (lexer.token() == FOR) {
                lexer.nextToken();
                if (lexer.token() == UPDATE) {
                    lexer.nextToken();
                }
            }
            if (lexer.token() == LOCK) {
                lexer.nextToken();
                if (lexer.token() == IN) {
                    lexer.nextToken();
                    if (lexer.token() == SHARE) {
                        lexer.nextToken();
                        if (lexer.token() == MODE) {
                            lexer.nextToken();
                        }
                    }
                }
            }
        }
    }

    public static void groupby_clause(Lexer lexer) {
        accept(lexer, Token.GROUP);
        accept(lexer, Token.BY);
        groupby_item(lexer);
        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            groupby_item(lexer);
        }
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            identifierEquals(lexer, "ROLLUP");
        }
    }

    public static void groupby_item(Lexer lexer) {

    }

    public static final void orderby_clause(Lexer lexer) {
        accept(lexer, Token.ORDER);
        accept(lexer, Token.BY);
        orderby_item(lexer);
        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            orderby_item(lexer);
        }
    }

    public static final void orderby_item(Lexer lexer) {
        accept(lexer, Token.ORDER);
        accept(lexer, Token.BY);
        if (lexer.token() == Token.ASC) {

        } else if (lexer.token() == Token.DESC) {

        }
        lexer.nextToken();
    }

    public static void having_clause(Lexer lexer) {
        accept(lexer, Token.HAVING);
        ExpMain.expression(lexer);
    }

    public static void limit_clause(Lexer lexer) {
        if (lexer.token() == LIMIT) {
            lexer.nextToken();
            lexer.integerValue();
            lexer.nextToken();
            if (lexer.token() == COMMA) {
                lexer.nextToken();
                lexer.integerValue();
                lexer.nextToken();
            } else if (identifierEquals(lexer, "OFFSET")) {
                lexer.nextToken();
                lexer.integerValue();
                lexer.nextToken();
            }
        }
    }

    static void select_list(Lexer lexer) {
        if (lexer.token() == Token.STAR) {
            return;
        } else {
            displayed_column(lexer);
            while (lexer.token() == Token.COMMA) {
                displayed_column(lexer);
            }
        }
    }


    static void displayed_column(Lexer lexer) {
        column_spec(lexer);
        alias(lexer);
    }

    static void partition_clause(Lexer lexer) {
        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            //跳过非必要的
            while (lexer.token() == Token.RPAREN) lexer.nextToken();
        }
    }

    public static final void where_clause(Lexer lexer) {
        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            ExpMain.expression(lexer);
        }
    }

    static void table_spec(Lexer lexer) {
        String schema_name = lexer.stringVal();
        String table_name;
        lexer.nextToken();
        if ((lexer.token() == Token.DOT)) {
            lexer.nextToken();
            table_name = lexer.stringVal();
            lexer.nextToken();
        } else {
            table_name = schema_name;
            schema_name = null;
        }
    }

    static void table_name(Lexer lexer) {

    }

    static void column_spec(Lexer lexer) {

    }

    static void index_hint_list(Lexer lexer) {
        index_hint(lexer);
        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            index_hint(lexer);
        }
    }

    static void index_hint(Lexer lexer) {
        boolean peekFlag = false;
        switch (lexer.token()) {
            case USE: {
                lexer.nextToken();
                peekFlag = true;
                break;
            }
            default:
                if (identifierEquals(lexer, "IGNORE")) {
                    lexer.nextToken();
                    peekFlag = true;

                } else if (identifierEquals(lexer, "FORCE")) {
                    peekFlag = true;
                    lexer.nextToken();
                }
        }
        if (peekFlag) {
            index_options(lexer);
            //跳过,直接跳到由括号
            while (lexer.token() != Token.RPAREN) lexer.nextToken();
        }
    }

    static void index_options(Lexer lexer) {
        switch (lexer.token()) {
            case INDEX:
            case KEY:
                lexer.nextToken();
        }
        if (lexer.token() == Token.FOR) {
            switch (lexer.token()) {
                case JOIN: {
                    break;
                }
                case ORDER: {
                    lexer.nextToken();
                    accept(lexer, Token.BY);
                    break;
                }
                case GROUP: {
                    lexer.nextToken();
                    accept(lexer, Token.BY);
                    break;
                }
            }
        }
    }

    static String alias(Lexer lexer) {
        String alias = null;
        if (lexer.token() == Token.LITERAL_ALIAS) {
            alias = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_CHARS) {
            alias = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        } else {
            switch (lexer.token()) {
                case KEY:
                case INDEX:
                case CASE:
                case MODEL:
                case PCTFREE:
                case INITRANS:
                case MAXTRANS:
                case SEGMENT:
                case CREATION:
                case IMMEDIATE:
                case DEFERRED:
                case STORAGE:
                case NEXT:
                case MINEXTENTS:
                case MAXEXTENTS:
                case MAXSIZE:
                case PCTINCREASE:
                case FLASH_CACHE:
                case CELL_FLASH_CACHE:
                case KEEP:
                case NONE:
                case LOB:
                case STORE:
                case ROW:
                case CHUNK:
                case CACHE:
                case NOCACHE:
                case LOGGING:
                case NOCOMPRESS:
                case KEEP_DUPLICATES:
                case EXCEPTIONS:
                case PURGE:
                case INITIALLY:
                case END:
                case COMMENT:
                case ENABLE:
                case DISABLE:
                case SEQUENCE:
                case USER:
                case ANALYZE:
                case OPTIMIZE:
                case GRANT:
                case REVOKE:
                case FULL:
                case TO:
                case NEW:
                case INTERVAL:
                case LOCK:
                case LIMIT:
                case IDENTIFIED:
                case PASSWORD:
                case BINARY:
                case WINDOW:
                case OFFSET:
                case SHARE:
                case START:
                case CONNECT:
                case MATCHED:
                case ERRORS:
                case REJECT:
                case UNLIMITED:
                case BEGIN:
                case EXCLUSIVE:
                case MODE:
                case ADVISE:
                case TYPE:
                case CLOSE:
                    alias = lexer.stringVal();
                    lexer.nextToken();
                    return alias;
                case QUES:
                    alias = "?";
                    lexer.nextToken();
                default:
                    break;
            }
        }
        return alias;
    }

    static String as(Lexer lexer) {
        String alias = null;

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            alias = alias(lexer);

            if (alias != null) {
                while (lexer.token() == Token.DOT) {
                    lexer.nextToken();
                    alias += ('.' + lexer.token().name());
                    lexer.nextToken();
                }

                return alias;
            }

            if (lexer.token() == Token.LPAREN) {
                return null;
            }

            throw new ParserException("Error : " + lexer.token());
        }

        if (lexer.token() == Token.LITERAL_ALIAS) {
            alias = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_CHARS) {
            alias = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        } else if (lexer.token() == Token.CASE) {
            alias = lexer.token.name();
            lexer.nextToken();
        } else if (lexer.token() == Token.USER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token() == Token.END) {
            alias = lexer.stringVal();
            lexer.nextToken();
        }

        switch (lexer.token()) {
            case KEY:
            case INTERVAL:
            case CONSTRAINT:
                alias = lexer.token().name();
                lexer.nextToken();
                return alias;
            default:
                break;
        }

        return alias;
    }

    protected static boolean identifierEquals(Lexer lexer, String text) {
        return lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase(text);
    }

    public static void accept(Lexer lexer, Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            //setErrorEndPos(lexer.pos());
            // printError(token);
        }
    }

    public static void match(Lexer lexer, Token token) {
        if (lexer.token() != token) {
            throw new ParserException("syntax error, expect " + token + ", actual " + lexer.token() + " "
                    + lexer.stringVal());
        }
    }
}
