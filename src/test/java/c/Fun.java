package c;



import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * Created by Administrator on 2016/12/31 0031.
 */
public class Fun {
    @FunctionalInterface
    public interface F<T, R> extends Function<T, R> {
    }

    @FunctionalInterface
    public interface Curry<T> extends F<T, F<T, F<T, T>>> {
    }

    public static void main(String[] args) {

//        Curry<Integer> iadd = x -> y -> z -> x + y + z;
//        Curry<String> stradd = x -> y -> z -> x + y + z;
//        val res = stradd.apply("a").apply("b").apply("c");
//        out.println(res);
//        val res2 = iadd.apply(1).apply(2).apply(3);
//        out.println(res2);
    }
}
