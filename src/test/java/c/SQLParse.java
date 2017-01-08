package c;



import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;
import java.util.function.Function;
import java.util.stream.Stream;

//http://dev.mysql.com/doc/refman/5.7/en/identifiers.html 表名命名标准

public class SQLParse <Int extends Integer>{

    static final byte CHAR_A = 0x41;
    static final byte CHAR_B = 0x42;
    static final byte CHAR_C = 0x43;
    static final byte CHAR_D = 0x44;
    static final byte CHAR_E = 0x45;
    static final byte CHAR_F = 0x46;
    static final byte CHAR_G = 0x47;
    static final byte CHAR_H = 0x48;
    static final byte CHAR_I = 0x49;
    static final byte CHAR_J = 0x4A;
    static final byte CHAR_K = 0x4B;
    static final byte CHAR_L = 0x4C;
    static final byte CHAR_M = 0x4D;
    static final byte CHAR_N = 0x4E;
    static final byte CHAR_O = 0x4F;
    static final byte CHAR_P = 0x50;
    static final byte CHAR_Q = 0x51;
    static final byte CHAR_R = 0x52;
    static final byte CHAR_S = 0x53;
    static final byte CHAR_T = 0x54;
    static final byte CHAR_U = 0x55;
    static final byte CHAR_V = 0x56;
    static final byte CHAR_W = 0x57;
    static final byte CHAR_X = 0x58;
    static final byte CHAR_Y = 0x59;
    static final byte CHAR_Z = 0x5A;

    static final byte CHAR_a = 0x61;
    static final byte CHAR_b = 0x62;
    static final byte CHAR_c = 0x63;
    static final byte CHAR_d = 0x64;
    static final byte CHAR_e = 0x65;
    static final byte CHAR_f = 0x66;
    static final byte CHAR_g = 0x67;
    static final byte CHAR_h = 0x68;
    static final byte CHAR_i = 0x69;
    static final byte CHAR_j = 0x6A;
    static final byte CHAR_k = 0x6B;
    static final byte CHAR_l = 0x6C;
    static final byte CHAR_m = 0x6D;
    static final byte CHAR_n = 0x6E;
    static final byte CHAR_o = 0x6F;
    static final byte CHAR_p = 0x70;
    static final byte CHAR_q = 0x71;
    static final byte CHAR_r = 0x72;
    static final byte CHAR_s = 0x73;
    static final byte CHAR_t = 0x74;
    static final byte CHAR_u = 0x75;
    static final byte CHAR_v = 0x76;
    static final byte CHAR_w = 0x77;
    static final byte CHAR_x = 0x78;
    static final byte CHAR_y = 0x79;
    static final byte CHAR_z = 0x7A;

    static final byte CHAR_DOT = ',';

    static int FROM_ID(final byte[] sql, short[] result, int pos, int result_pos) {
        Runnable s=()->{};
        s.run();;
        int SQLLength = sql.length - 1;
        short result_size = 0;
        boolean isDot = true;
        do {
            switch (sql[pos]) {
                case CHAR_F:
                    if (sql[++pos] == CHAR_R && sql[++pos] == CHAR_O && sql[++pos] == CHAR_M &&
                            (sql[++pos] == 0x20 || sql[pos] == 0x0D || sql[pos] == 0x0A)) {
                        ///////////////////////////////////////////////////////
                        //下一步代码块
                        out_finder:
                        do {
                            switch (sql[++pos]) {
                                case 0x20:
                                case 0x0D:
                                case 0x0A:
                                    break;
                                case ')':
                                    //空格 联合union
                                    switch (sql[pos]) {
                                        case 'u':
                                    }
                                    return pos;
                                case '(':
                                    pos = FROM_ID(sql, result, pos, result_size);
                                    break;
                                case ',':
                                    isDot = true;
                                    break;
                                default:
                                    if (isDot) {
                                        //result[result_pos++] = (short) pos;
                                        result_size = 1;
                                        inner_finder:
                                        while (pos < SQLLength) {
                                            switch (sql[++pos]) {
                                                case 0x20:
                                                case 0x0D:
                                                case 0x0A:
                                                    if (result_size > 0) {
                                                        print(pos, result_size);
                                                    }
                                                    result_size = 0;
                                                    continue out_finder;
                                                case ',':
                                                    isDot = true;
                                                    print(pos, result_size);
                                                    continue out_finder;
                                                case ')':
                                                    break inner_finder;
                                                default:
                                                    result_size++;
                                            }
                                        }
                                        //   result[result_pos++] = result_size;
                                        isDot = false;
                                        print(pos, result_size);
                                        continue out_finder;
                                    }

                            }
                            //下一步代码块
                            ///////////////////////////////////////////
                        } while (pos < SQLLength);
                    }
                    break;
                case 0x20:
                case 0x0D:
                case 0x0A:
                    //while((sql[++pos]==0x20 || sql[pos]==0x0D || sql[pos]==0x0A) && pos<SQLLength); //飞略换行、空格
                    pos++;
                    break;
                default:
                    //while(sql[++pos]!=0x20 && sql[pos]!=0x0D && sql[pos]!=0x0A && pos<SQLLength); //飞略字符串
                    pos++;
            }
        } while (pos < SQLLength);
        return pos;
    }

    static void print(int pos, int result_size) {
        System.out.println(pos + " " + result_size);
    }

    static long RunBench() {
        short[] result = new short[128];
        //  todo:((SELECT a FROM (SELECT a FROM b6,dv)) union (SELECT a FROM sss))"
        //  todo:栈
        //  todo:越过字符串
        //  todo:大小写
        //  todo:动态解析代码生成器
        //  todo:函数调用
        //  tip:sql越长时间越长
        //  tip:递归好像消耗有点大
        byte[] src = "SELECT a FROM ab            ,ff,(SELECT a FROM bb,(SELECT a FROM ccc,dddd)),eeeee".getBytes(StandardCharsets.UTF_8);//20794
        int count = 0;
        long start = System.currentTimeMillis();
        do {
            FROM_ID(src, result, 0, 0);//RAW:3244 3260  KMP:3900 3884 ;两层嵌套sql递归有返回值:10078 三层嵌套sql递归值返回:17893 18033
        } while (count++ < 10000 * 10000);
        return System.currentTimeMillis() - start;
    }


    public static void main(String[] args) {


        long min = 0;
        for (int i = 0; i < 10; i++) {
            long cur = RunBench();
            if (cur < min || min == 0) {
                min = cur;
            }
        }
        System.out.print(min);
    }
}
