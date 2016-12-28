import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class KPM {

    /**
     * Search the data byte array for the first occurrence
     * of the byte array pattern.
     */
    public static int indexOf(byte[] data, byte[] pattern) {
        int[] failure = computeFailure(pattern);
        return indexOf(data, 0, pattern, failure);
    }

    public static int indexOf(byte[] data, int start, byte[] pattern, int[] failure) {
        int j = 0;
        for (int i = start; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

    public static BMatch of(byte[] pattern) {
        return new BMatch(pattern);
    }

    public static BMatch of(String pattern, Charset charset) {
        return new BMatch(pattern.getBytes(charset));
    }

    interface Match {
        public int indexOf(byte[] data);

        public int indexOf(byte[] data, int start);
    }

    static public class BMatch implements Match {
        int[] failure;
        byte[] pattern;
        int type=this.hashCode();

        public int getType() {
            return type;
        }

        public BMatch(byte[] pattern) {
            this.pattern = pattern;
            this.failure = computeFailure(pattern);
        }

        public int indexOf(byte[] data) {
            return KPM.indexOf(data, 0, pattern, failure);
        }

        public int indexOf(byte[] data, int start) {
            return KPM.indexOf(data, start, pattern, failure);
        }

        public byte[] getPattern() {
            return pattern;
        }
        public int getPatternSize() {
            return pattern.length;
        }
    }


    public static void main(String[] args) {
//        Function<String, byte[]> encode = String::getBytes;
//        Match match = KPM.of("6", StandardCharsets.UTF_8);
//        Match space = KPM.of(" ", StandardCharsets.UTF_8);
//        int res = match.indexOf("select d".getBytes(), 5);
//        System.out.println(res);

    }
}