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
     *
     * @param data 解析的byte数组
     * @param start 开始位置
     * @param end 结束位置
     * @param peekLocation 预读队列,int[0]为开始token开始的位置,int[1]为结束的位置
     * @param keywords 关键字
     * @param skip 跳过的关键字
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
     * peekLocation[i][0] == 0 即还没有预读
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
     *
     * @return
     */
    public int[] token() {
        if (peekLocation[0][0] == 0) {
            fill(0);
        }
        return peekLocation[0];

    }

    /**
     * 吃掉peekLocation第一个,之后后面元素的往前移动
     */
    public void next() {
        for (int i = 0; i < peekLocation.length - 1; i++) {
            int[] tmp = peekLocation[i + 1];
            if (tmp[0] != 0) {
                peekLocation[i] = tmp;
            } else {
                break;
            }
        }
        int[] end = peekLocation[peekLocation.length];
        end[0] = 0;
        end[1] = 0;
        if (peekLocation[0][0] == 0) {
            fill(0);
        }
    }

    /**
     *
     * @return
     */
    private boolean isWhitespace(){
        for (int j = 0; j< skipList.length; j++) {

        }
        return false;
    }

    /**
     *
     * @param n peekLocation的位置
     */
    private void fill(int n) {
        skip();
        for (int i = 0; i < matchesList.length; i++) {
            int pos = matchesList[i].indexOf(data, start);

            if (start == pos) {


             /**没有完成**/


                tokenTypeQueue[n] = matchesList[i];
                peekLocation[n][0] = pos;
                peekLocation[n][1] = pos+matchesList[i].getPatternSize();
                break;
            }
        }
    }

    /**
     * 跳过
     */
    public void skip() {
        for (int i = 0; i < skipList.length; i++) {
            int res = skipList[i].indexOf(data, start);
            if ((skipList[i].getPatternSize() + start) == res) {
                i = 0;
                start = res;
            }
        }
    }

    /**
     * 工厂函数
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

    public static void main(String[] args) {
        byte[] data = "select a from b".getBytes();
        byte[][] keywords = new byte[2][];
        byte[][] skip = new byte[1][];
        keywords[0] = "select".getBytes();
        keywords[1] = "from".getBytes();
        skip[0] = " ".getBytes();
        ByteArrayLexer lexer = ByteArrayLexer.of(data, 0, 0, keywords, skip);
        out.println(lexer.token());
        lexer.next();
        out.println(lexer.token());
    }
}
