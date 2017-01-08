import c.KPM;
import sun.nio.cs.ArrayDecoder;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.lang.System.out;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class ByteArrayLexer {
    int[][] peekLocation;
    /**
     * 对应peekLocation,peekLocation里面的是位置
     * tokenTypeQueue里面的是匹配器,兼做类型记号getType
     */
    KPM.BMatch[] tokenTypeQueue;
    byte[] data;
    int start;
    int end;
    int peek;
    KPM.BMatch[] matchesList;
    KPM.BMatch[] skipList;

    /**
     * @param data         解析的byte数组
     * @param start        开始位置
     * @param end          结束位置
     * @param peekLocation 预读队列,int[0]为开始token开始的位置,int[1]为结束的位置
     * @param keywords     关键字
     * @param skip         跳过的关键字
     */
    public ByteArrayLexer(byte[] data, int start, int end, int[][] peekLocation, byte[][] keywords, byte[][] skip) {
        this.peekLocation = peekLocation;
        this.tokenTypeQueue = new KPM.BMatch[peekLocation.length];
        this.data = data;
        this.start = start;
        this.end = end;
        this.peek = 0;
        this.matchesList = new KPM.BMatch[keywords.length];
        this.skipList = new KPM.BMatch[skip.length];
        for (int i = 0; i < keywords.length; i++) {
            matchesList[i] = KPM.of(keywords[i]);
        }
        for (int j = 0; j < skip.length; j++) {
            skipList[j] = KPM.of(skip[j]);
        }
    }

    /**
     * 预读几个,填充到peekLocation里,返回peekLocation
     * peekLocation[i][1] == 0 即还没有预读
     *
     * @param n
     * @return
     */
    public int[][] peek(int n) {
        if (peekLocation[n][0] == 0) {
            int i = 0;
            //find empty
            for (; i < peekLocation.length; i++) {
                if (peekLocation[i][0] == 0) {
                    break;
                }
            }
            for (int j = i; j < n; j++) {
                fill(j);
            }
        }
        return peekLocation;
    }

    /**
     * @return
     */
    public int[] token() {
        if (peekLocation[0][1] == 0) {
            fill(0);
        }
        return peekLocation[0];

    }

    /**
     * 吃掉peekLocation第一个,之后后面元素的往前移动
     */
    public void next() {
        int i = 0;
        peekLocation[0][1] = 0;
        for (; i < peekLocation.length - 1; i++) {
            int[] tmp = peekLocation[i + 1];
            if (tmp[1] != 0) {
                peekLocation[i] = tmp;
                tokenTypeQueue[i] = tokenTypeQueue[i + 1];
            } else {
                peek = i + 1;
                break;
            }
        }
        for (int j = peekLocation.length - 1; j > i; --j) {
            int[] end = peekLocation[j];
            end[0] = 0;
            end[1] = 0;
            tokenTypeQueue[j] = null;
        }

        if (peekLocation[0][1] == 0) {
            peek = 0;
            fill(0);
        }
    }

    /**
     * @return
     */
    private boolean isWhitespace() {
        return data[start] == 32;
    }

    public int[] stringVal() {
        skip();
        int i = start;
        while (data[start++] != 32 && start <= end) {

        }
        peek++;
        peekLocation[peek][0] = i;
        peekLocation[peek][1] = start;
        return peekLocation[peek];
    }

    /**
     * @param n peekLocation的位置
     */
    private void fill(int n) {
        skip();
        for (int i = 0; i < matchesList.length; i++) {
            int pos = matchesList[i].indexOf(data, start);
            int end = pos + matchesList[i].getPatternSize();
            if (data[end] == 32) {
                /**没有完成**/
                start = end;
                tokenTypeQueue[n] = matchesList[i];
                peekLocation[n][0] = pos;
                peekLocation[n][1] = end;
                peek++;
                break;
            }
        }
    }

    /**
     * 跳过
     */
    public void skip() {
        while (data[this.start] == 32 && start <= end) {
            ++this.start;
        }
    }

    /**
     * 工厂函数
     *
     * @param data
     * @param start
     * @param end
     * @param keywords
     * @param skip
     * @return
     */
    public static ByteArrayLexer of(byte[] data, int start, int end, byte[][] keywords, byte[][] skip) {
        return new ByteArrayLexer(data, start, end, new int[4][2], keywords, skip);
    }


    public int[] res = new int[]{0, 0};
    public byte[] SELECT = "SELECT".getBytes();

    public int[] SELECT() {
        return SELECT(data, res[0], res[1]);
    }

    /**
     * 准备用于生产  函数模板,第二种解析方式 用于在语法分析中马上确定下一步是啥,而不是让lexer瞎尝试
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    public int[] SELECT(byte[] data, int start, int end) {
        int lengh = SELECT.length;
        if (end - start >= lengh) {
            if (data[start] == SELECT[1] && data[start + 1] == SELECT[1]) {
                res[0] = start;
                res[1] = start + lengh;
//                    tokenQueue[tokenIndex] = SELECT;
//                    ++tokenIndex;
            } else {
                res[0] = -1;
            }
        } else {
            res[0] = -2;
        }
        return res;
    }

    private static int scale(int len, float expansionFactor) {
        // We need to perform double, not float, arithmetic; otherwise
        // we lose low order bits when len is larger than 2**24.
        return (int) (len * (double) expansionFactor);
    }

    public static void main(String[] args) throws Exception {
        byte[] data = "select a from b ".getBytes(StandardCharsets.UTF_8);
        int sum = 0;
        CharsetDecoder cd = StandardCharsets.UTF_8.newDecoder();
        int en = scale(data.length, cd.maxCharsPerByte());
        char[] ca = new char[8192];
        ArrayDecoder ad = (ArrayDecoder) cd;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000 * 10000; i++) {
            // int clen = ad.decode(data, 0, data.length, ca);
            // sum += new String(data,StandardCharsets.UTF_8).length();//5700
            int c = ad.decode(data, 0, data.length, ca);
            sum += c;//2800
        }
        out.println(sum + "  " + String.valueOf(System.currentTimeMillis() - start));

        byte[][] keywords = new byte[2][];
        byte[][] skip = new byte[1][];
        keywords[0] = "select".getBytes();
        keywords[1] = "from".getBytes();
        skip[0] = " ".getBytes();
        ByteArrayLexer lexer = ByteArrayLexer.of(data, 0, 0, keywords, skip);
        out.println(Arrays.toString(lexer.token()));
        lexer.next();
        out.println(Arrays.toString(lexer.stringVal()));
        out.println(Arrays.toString(lexer.token()));
        lexer.next();
        out.println(Arrays.toString(lexer.stringVal()));
        //  out.println(Arrays.toString(c.Lexer.SELECT(data,0,data.length)));

    }
}
