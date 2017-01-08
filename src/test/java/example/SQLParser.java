package example;

import java.nio.charset.StandardCharsets;

//http://dev.mysql.com/doc/refman/5.7/en/identifiers.html 表名命名标准
//https://github.com/mysql/mysql-workbench/blob/master/library/mysql.parser/grammar/MySQL.g
//by kaiz : 需要处理的包含表名的文法，可能还有遗漏，随时补充，已经实现的部分请去掉前面 // 注释
//RENAME_SYMBOL (TO_SYMBOL | AS_SYMBOL)? table_ref

//create_index_target:
//    ON_SYMBOL^ table_ref index_columns

//OPEN_PAR_SYMBOL LIKE_SYMBOL table_ref CLOSE_PAR_SYMBOL

//ON_SYMBOL table_ref

//if_exists? table_ref_list

//RENAME_SYMBOL^ (TABLE_SYMBOL | TABLES_SYMBOL)
//table_ref TO_SYMBOL table_name (COMMA_SYMBOL table_ref TO_SYMBOL table_name)*

//TRUNCATE_SYMBOL^ TABLE_SYMBOL? table_ref

//delete_statement:
//	DELETE_SYMBOL^ ( options { greedy = true; }: delete_option)*
//		(
//			FROM_SYMBOL
//				// Both alternatives can start with identifier DOT.
//				( options { k = 4; }:
//					 table_ref_list_with_wildcard USING_SYMBOL join_table_list where_clause? // Multi table variant 1.
//					| table_ref partition_delete? where_clause? order_by_clause? simple_limit_clause? // Single table delete.
//				)
//			|  table_ref_list_with_wildcard FROM_SYMBOL join_table_list where_clause? // Multi table variant 2.
//		)

//handler_statement:
//	HANDLER_SYMBOL^
//	(  options { k = 4; }:
//		table_ref OPEN_SYMBOL (AS_SYMBOL? identifier)?
//		| table_ref_no_db
//			(
//				| CLOSE_SYMBOL
//				| READ_SYMBOL handler_read_or_scan where_clause? limit_clause?
//			)
//	)

//INSERT_SYMBOL^ insert_lock_option? IGNORE_SYMBOL? INTO_SYMBOL? table_ref use_partition?

//REPLACE_SYMBOL^ (LOW_PRIORITY_SYMBOL | DELAYED_SYMBOL)? INTO_SYMBOL? table_ref

//JOIN_SYMBOL table_reference ( options {greedy = true;}: join_condition)?

//LOAD_SYMBOL^ (DATA_SYMBOL | TABLE_SYMBOL table_ref) FROM_SYMBOL MASTER_SYMBOL

//(INDEX_SYMBOL | INDEXES_SYMBOL | KEYS_SYMBOL) from_or_in table_ref in_db? where_clause?

//FULL_SYMBOL? COLUMNS_SYMBOL (FROM_SYMBOL | IN_SYMBOL) table_ref in_db? like_or_where?

//REFERENCES_SYMBOL^ table_ref index_columns?

//DESCRIBE_SYMBOL^ | DESC_SYMBOL^ ( table_ref (text_string | identifier)?

//LOAD_SYMBOL^ INDEX_SYMBOL INTO_SYMBOL CACHE_SYMBOL table_ref load_table_index_partion?

//UNION_SYMBOL^ EQUAL_OPERATOR? OPEN_PAR_SYMBOL table_ref_list CLOSE_PAR_SYMBOL

//lock_statement:
//	LOCK_SYMBOL^ (TABLES_SYMBOL | TABLE_SYMBOL) lock_item (COMMA_SYMBOL lock_item)*
//	| UNLOCK_SYMBOL^ (TABLES_SYMBOL | TABLE_SYMBOL)
//lock_item:
//	table_ref table_alias? lock_option
//lock_option:
//	READ_SYMBOL LOCAL_SYMBOL?
//	| LOW_PRIORITY_SYMBOL? WRITE_SYMBOL // low priority deprecated since 5.7

//table_ref_with_wildcard:
//        identifier
//        (
//        {LA(2) == MULT_OPERATOR}? DOT_SYMBOL MULT_OPERATOR
//        | dot_identifier (DOT_SYMBOL MULT_OPERATOR)?
//        )?
//        ;
//
//        table_ref_no_db:
//        identifier -> ^(TABLE_REF_TOKEN identifier)
//        ;
//
//        table_ref_list_with_wildcard:
//        table_ref_with_wildcard (COMMA_SYMBOL table_ref_with_wildcard)*
//        ;

/*
by kaiz : 考虑程序的基本架构：所有标签的解析都分为 定位（FINDER）- 处理（PARSER）两个状态，
在定位过程中需要跳过空格、换行或可能存在的修饰符直接定位到内容，如果不存在的话则根据下一个节点决定状态机的切换
处理过程相对都会比较简单一些

SELECT FOR UPDATE
switch (status_queue[queue_pos]) {
    case BASIC_PARSER:
        //遍历基础字符串
        //每次进入 BASIC_PARSER 时需要重置 status_queue 与 queue_pos
        basic_loop:
        while () {
            switch (sql[pos]) {
                case F:
                    //Find_ID 函数
                    1. 跳过空格、换行
                    2. 如果遍历到 ( 说明有子查询，break 后继续按照普通sql处理
                    3. 其他情况，break basic_loop ，将 TBL_NAME_FINDER 、TBL_NAME_PARSER 、TBL_ALIAS_FINDER 、TBL_ALIAS_PARSER 、 TBL_COMMA_FINDER *按顺序*压入 status_queue 队列中
                case 空格换行:
                    直接略过
                case ":
                    //略过字符串，注意转义字符
                case UPDATE\JOIN\DELETE\DROP\INTO\TABLE 关键字处理，都可以直接跳转 TBL_NAME_FINDER 状态
                    另外需要根据文法规则决定是否需要进入诸如 TBL_ALIAS_FINDER 之类的其它标签查找状态
                    如果之后可能链接多个标签，则*逆序*将需要处理的状态添加到 status_queue 中，用状态机解决函数调用问题
            }
        }
    case TBL_NAME_FINDER:
        //定位表名，主要是跳过表名前的空格和 ` ，
        //定位到表名后跳转 TBL_NAME_PARSE
        //如果没有定位到表名，则直接返回 BASIC_PARSER 流程
    case TBL_NAME_PARSER:
        //处理表名
        //table_ref use_partition? table_alias? index_hint_list?
        1. 处理库名 scheme_name.table_name
        2. 处理 `
        3. 如果在解析到的表名后有 . 句号，则说明之前解析到的表名其实是库名，定位到 . 之后重新开始解析表名
        4. 在表名处理完后，将 status_pos++ 进入接下来的流程
    case TBL_ALIAS_FINDER:
        //定位别名
        //只有遇到 AS 才会跳转 TBL_ALIAS_PARSE
        //如果遇到 , 逗号，则跳转回 TBL_NAME_FINDER
        //没有别名解析的话，根据 status_queue 进入接下来的流程
    case TBL_ALIAS_PARSER:
        //处理别名
        //可能包含别名，也可能不包含别名
        //处理完别名之后，根据 status_queue 进入接下来的流程
    case TBL_COMMA_FINDER:
        //处理 ,
        //如果存在逗号，则将 queue_pos 重新置回栈顶位置，重新进入状态流程循环
}

 */

public class SQLParser {
    static final byte BASIC_PARSER = 0;
    static final byte TBL_NAME_FINDER = 1;
    static final byte TBL_NAME_PARSER = 2;
    static final byte TBL_ALIAS_FINDER = 3;
    static final byte TBL_ALIAS_PARSER = 4;
    static final byte TBL_COMMA_FINDER = 5;
    static final byte QUEUE_SIZE = 16;
    static byte[] status_queue = new byte[QUEUE_SIZE]; //by kaiz : 为扩展复杂的解析预留空间，考虑到表名之后的修饰token可能无法预期，将可能需要处理的步骤状态值压入队列中，再从队列中逐一处理

    static void SQLParse(final byte[] sql, int[] result) {
        int SQLLength = sql.length - 1;
        int pos = 0;
        int result_pos = 0;
        short result_size = 1;
        byte queue_pos = 0;
        while (pos < SQLLength) {  //by kaiz : 考虑到将来可能要用unsafe直接访问，所以越界判断都提前了
            switch (status_queue[queue_pos]) {
                case BASIC_PARSER:
                    //by kaiz : 清空数组，有没有
                    for(queue_pos=0; queue_pos<QUEUE_SIZE && status_queue[queue_pos]!=BASIC_PARSER; status_queue[queue_pos++]=BASIC_PARSER);
                    queue_pos = 0;
                    basic_loop:
                    while (pos < SQLLength) {
                        switch (sql[pos++]) {
                            case 'F':
                                if (sql[pos++] == 'R' && sql[pos++] == 'O' && sql[pos++] == 'M' &&  //by kaiz : 标准 FROM
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    status_queue[2] = TBL_ALIAS_FINDER;
                                    status_queue[3] = TBL_ALIAS_PARSER;
                                    status_queue[4] = TBL_COMMA_FINDER;
                                    break basic_loop;
                                }
                                break;
                            case 'f':
                                if (sql[pos++] == 'r' && sql[pos++] == 'o' && sql[pos++] == 'm' &&  //by kaiz : 标准 from
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    status_queue[2] = TBL_ALIAS_FINDER;
                                    status_queue[3] = TBL_ALIAS_PARSER;
                                    status_queue[4] = TBL_COMMA_FINDER;
                                    break basic_loop;
                                }
                                break;
                            case 'J':
                                if (sql[pos++] == 'O' && sql[pos++] == 'I' && sql[pos++] == 'N' &&  //by kaiz : 标准 JOIN
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'j':
                                if (sql[pos++] == 'o' && sql[pos++] == 'i' && sql[pos++] == 'n' &&  //by kaiz : 标准 join
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'U':
                                if (sql[pos++] == 'P' && sql[pos++] == 'D' && sql[pos++] == 'A' && sql[pos++] == 'T' && sql[pos++] == 'E' &&  //by kaiz : 标准 UPDATE
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'u':
                                if (sql[pos++] == 'p' && sql[pos++] == 'd' && sql[pos++] == 'a' && sql[pos++] == 't' && sql[pos++] == 'e' &&  //by kaiz : 标准 UPDATE
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'I':
                                if (sql[pos++] == 'N' && sql[pos++] == 'T' && sql[pos++] == 'O' &&  //by kaiz : 标准 INTO
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'i':
                                if (sql[pos++] == 'n' && sql[pos++] == 't' && sql[pos++] == 'o' &&  //by kaiz : 标准 into
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 'T':
                                if (sql[pos++] == 'A' && sql[pos++] == 'B' && sql[pos++] == 'L' &&sql[pos++] == 'E' &&  //by kaiz : 标准 TABLE(S)
                                        ((sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n') || sql[pos] == 'S')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case 't':
                                if (sql[pos++] == 'a' && sql[pos++] == 'b' && sql[pos++] == 'l' &&sql[pos++] == 'e' &&  //by kaiz : 标准 table(s)
                                        ((sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n') || sql[pos] == 's')) {
                                    pos++;
                                    //by kaiz : 将接下来需要处理的状态按顺序加入队列
                                    status_queue[0] = TBL_NAME_FINDER;
                                    status_queue[1] = TBL_NAME_PARSER;
                                    break basic_loop;
                                }
                                break;
                            case ' ':
                            case '\r':
                            case '\n':
                                break;
                        }
                    }
                    break;
                case TBL_NAME_FINDER:
                    finder_loop:
                    while (pos < SQLLength) {
                        switch (sql[pos++]) {  //过滤FROM和表名之前的空格
                            case ' ':
                            case '\r':
                            case '\n':
                            case '`':
                                break;
                            case '(':
                                status_queue[queue_pos] = BASIC_PARSER; //如果是括号说明是子查询，直接回到正常解析
                                break finder_loop;
                            case ';':
                                status_queue[queue_pos] = BASIC_PARSER;
                                break finder_loop;
                            default:
                                result[result_pos++] = pos-1;
                                result_size=1;
                                queue_pos++;
                                break finder_loop; //by kaiz : 还需要增加 from a as aa, b as bb的逻辑
                        }
                    }
                    break;
                case TBL_NAME_PARSER:
                    name_loop:
                    while (pos < SQLLength) {
                        switch (sql[pos++]) { //by kaiz : 检查表名之后结束串
                            case ' ':
                            case '\r':
                            case '\n':
                            case '`':
                            case '(':
                            case ')':
                            case ';':
                                queue_pos++;
                                result[result_pos++] = result_size;
                                break name_loop;
                            case ',':
                                queue_pos=0;
                                result[result_pos++] = result_size;
                                break name_loop;
                            default:
                                result_size++;
                        }
                    }
                    break;
                case TBL_ALIAS_FINDER: //by kaiz : 因为可能存在 `schema_name`.`table_name` 或者 table_a as AA, table_b as BB 这种形式的，所以用TBLNAME_TAIL部分用来处理这部分数据
                    alias_loop:
                    while (pos < SQLLength) {
                        switch (sql[pos++]) {
                            case 'A': //by kaiz : 处理 AS 写法
                                if (sql[pos++]=='S' &&
                                        (sql[pos] == ' ' || sql[pos] == '\r' || sql[pos] == '\n')) {
                                    pos++;
                                    queue_pos++;
                                } else {
                                    status_queue[queue_pos] = BASIC_PARSER;
                                }
                                break alias_loop;
                            case ',':
                                queue_pos=0; //by kaiz : 和 TBL_COMMA_FINDER 一样的处理，回到队列首，重新开始下一个处理循环
                                break alias_loop;
                            case '.': //by kaiz : 处理 . 写法
                                //by kaiz : 回退之前的库名，别以为是重复了T_T
                                result[--result_pos]=0;
                                result[--result_pos]=0;
                                while (status_queue[--queue_pos]!=TBL_NAME_FINDER && queue_pos>0);
                                break alias_loop;
                            case ' ':
                            case '\r':
                            case '\n':
                                break;
                            default:
                                status_queue[queue_pos] = BASIC_PARSER;
                                break alias_loop;
                        }
                    }
                    break;
                case TBL_ALIAS_PARSER:
                    alias_loop:
                    while (pos < SQLLength) { //by kaiz : 略过 AS 后空格
                        switch (sql[pos++]) {
                            case ' ':
                            case '\r':
                            case '\n':
                                break;
                            default:
                                while (pos < SQLLength) { //by kaiz : 略过别名
                                    switch (sql[pos++]) {
                                        case ' ':
                                        case '\r':
                                        case '\n':
                                            queue_pos++;
                                            break alias_loop;
                                        case ',':
                                            queue_pos = 0;
                                            break alias_loop;
                                    }
                                }
                                break alias_loop;
                        }
                    }
                    break;
                case TBL_COMMA_FINDER:
                    comma_loop:
                    while (pos < SQLLength) {
                        switch (sql[pos++]) {
                            case ' ':
                            case '\r':
                            case '\n':
                                break ;
                            case ',':
                                queue_pos=0; //by kaiz : 回到队列首，重新开始下一个处理循环
                                break comma_loop;
                            default:
                                status_queue[queue_pos] = BASIC_PARSER; //by kaiz : 回到基本处理流程
                        }
                    }
                    break;
                default:
            }
        };
    }

    static int runBenchCount = 1;//1*1000*1000;
    static int mainLoopCount = 1;//3;
    static long RunBench() {
        int[] result = new int[128];
        //byte[] src = "SELECT a FROM ab             , `ff` AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));".getBytes(StandardCharsets.UTF_8); //36730
        //byte[] src = "SELECT * FROM tbl_a a LEFT JOIN tbl_b b ON b.id = a.id RIGHT JOIN tbl_c c ON c.id=b.id;".getBytes(StandardCharsets.UTF_8); //36730
        //byte[] src = "INSERT INTO tbl_abc(a, b, c) VALUES(aa, bb, cc)".getBytes(StandardCharsets.UTF_8); //36730
        //byte[] src = "UPDATE tbl_abc SET a=b WHERE id=1234;".getBytes(StandardCharsets.UTF_8); //36730
        //byte[] src = "LOCK TABLE tbl_abc READ;".getBytes(StandardCharsets.UTF_8); //36730
        byte[] src = "".getBytes(StandardCharsets.UTF_8); //36730

        //byte[] src = "SELECT a FROM b".getBytes(StandardCharsets.UTF_8); //2629
        long start = System.currentTimeMillis();
        int count = 0;
        while(count++ < runBenchCount) {
            SQLParse(src, result);
        }
        int i=0;
        while (result[i]!=0) { //by kaiz : 检查结果
            System.out.print(result[i++]+", ");
        }
        return System.currentTimeMillis()-start;
    }

    public static void main(String[] args) {
        long min = 0;
        for(int i=0; i<mainLoopCount; i++) {
            long cur = RunBench();
            if (cur<min||min==0) {
                min = cur;
            }
            System.out.println(i);
        }
        System.out.println("min time : "+min);
    }


}
