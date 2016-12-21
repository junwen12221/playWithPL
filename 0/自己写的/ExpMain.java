package com.alibaba.druid;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

import static com.alibaba.druid.Main.select_statement;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class ExpMain {
    public static void main(String[] args) {

    }

    public static final void expression(Lexer lexer) {
        exp_factor1(lexer);
        while (true) {
            if (lexer.token() != Token.OR) {
                lexer.nextToken();
                break;
            }
        }
    }

    public static final void exp_factor1(Lexer lexer) {
        exp_factor2(lexer);
        while (true) {
            if (lexer.token() != Token.XOR) {
                lexer.nextToken();
                break;
            }
        }
    }

    public static final void exp_factor2(Lexer lexer) {
        exp_factor3(lexer);
        while (true) {
            if (lexer.token() != Token.AND) {
                lexer.nextToken();
                break;
            }
        }
    }

    public static final void exp_factor3(Lexer lexer) {
        if (lexer.token() == Token.NOT) {
            lexer.nextToken();
        }
        exp_factor4(lexer);
    }

    public static final void exp_factor4(Lexer lexer) {
        bool_primary(lexer);
        if (lexer.token() == Token.IS) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
            }
            switch (lexer.token()) {
                case TRUE:
                case FALSE:
                case NULL:
                default:
                    lexer.nextToken();
            }
        }
    }

    public static final void bool_primary(Lexer lexer) {

    }

    public static final void predicate(Lexer lexer) {
        bit_expr(lexer);
        if (lexer.token() == Token.NOT) {
            lexer.nextToken();

        } else if (identifierEquals(lexer, "SOUNDS")) {
            lexer.nextToken();
            accept(lexer, Token.LIKE);
            bit_expr(lexer);
            return;
        }
        switch (lexer.token()) {
            case IN: {
                lexer.nextToken();

                break;
            }
            case BETWEEN: {
                lexer.nextToken();
                bit_expr(lexer);
                accept(lexer, Token.AND);
                predicate(lexer);
                break;
            }
            case LIKE: {
                lexer.nextToken();
                simple_expr(lexer);
                if (lexer.token() == Token.ESCAPE) {
                    simple_expr(lexer);
                }
                break;
            }
            default: {
                if (identifierEquals(lexer, "REGEXP")) {
                    lexer.nextToken();
                    bit_expr(lexer);
                } else {
                    //异常
                }
            }
        }
    }

    public static final void function_call(Lexer lexer) {


    }

    public static final void functionList(Lexer lexer) {

    }

    public static final void column_spec(Lexer lexer) {
        //检查是否是字符常量
        String first = lexer.stringVal();
        lexer.nextToken();
        if (lexer.token() == Token.DOT) {
            lexer.nextToken();
            String second = lexer.stringVal();
            lexer.nextToken();
            if (lexer.token() == Token.DOT) {
                String third = lexer.stringVal();
                lexer.nextToken();
            }
        } else {

        }
    }

    public static final void simple_expr(Lexer lexer) {
        switch (lexer.token()) {
            case INTERVAL: {
                interval_expr(lexer);
                return;
            }
            case ROW: {
                lexer.nextToken();
                expression_list(lexer);
                return;
            }
            case LPAREN: {
                expression_list(lexer);
            }
            default: {
                column_spec(lexer);
            }
        }
    }




    public static final void subquery(Lexer lexer) {
        accept(lexer, Token.LPAREN);
        select_statement(lexer);
        accept(lexer, Token.RPAREN);
    }

    public static final void expression_list(Lexer lexer) {
        accept(lexer, Token.LPAREN);
        expression(lexer);
        while (true) {
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                expression(lexer);
            } else {
                break;
            }
        }
        accept(lexer, Token.RPAREN);
    }

    public static final void interval_expr(Lexer lexer) {
        accept(lexer, Token.INTERVAL);
        expression(lexer);
        //interval_unit
        lexer.nextToken();

    }

    public static final void bit_expr(Lexer lexer) {
        factor1(lexer);
        if (identifierEquals(lexer, "VERTABAR")) {
            lexer.nextToken();
            factor1(lexer);
        }
    }

    public static final void factor1(Lexer lexer) {
        factor2(lexer);
        if (identifierEquals(lexer, "BITAND")) {
            lexer.nextToken();
            factor2(lexer);
        }
    }

    public static final void factor2(Lexer lexer) {
        factor3(lexer);
        if (identifierEquals(lexer, "<<") || identifierEquals(lexer, ">>")) {
            lexer.nextToken();
            factor3(lexer);
        }
    }

    public static final void factor3(Lexer lexer) {
        factor4(lexer);
        if (lexer.token() == Token.PLUS || lexer.token() == Token.MINUS) {
            lexer.nextToken();
            factor4(lexer);
        }
    }

    public static final void factor4(Lexer lexer) {
        factor5(lexer);
        if (identifierEquals(lexer, "ASTERISK")
                || lexer.token() == Token.DIV
                || lexer.token() == Token.MOD
                || identifierEquals(lexer, "POWER")) {
            lexer.nextToken();
            factor5(lexer);
        }
    }

    public static final void factor5(Lexer lexer) {
        factor6(lexer);
        if (lexer.token() == Token.PLUS || lexer.token() == Token.MINUS) {
            lexer.nextToken();
            accept(lexer, Token.INTERVAL);
        }
    }

    public static final void factor6(Lexer lexer) {
        switch (lexer.token()) {
            case PLUS:
            case MINUS:
            case TILDE://NEGATION
            case BINARY:
                lexer.nextToken();
            default:
        }
        simple_expr(lexer);
    }

    public static final void factor7(Lexer lexer) {
        simple_expr(lexer);
        if (identifierEquals(lexer, "COLLATE")) {
            lexer.nextToken();
            collation_names(lexer);
        }
    }

    public static final void collation_names(Lexer lexer) {
        if (identifierEquals(lexer, "LATIN1_GENERAL_CS") ||
                identifierEquals(lexer, "LATIN1_BIN")) {
            lexer.nextToken();
        } else {
            //异常
        }
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
}
