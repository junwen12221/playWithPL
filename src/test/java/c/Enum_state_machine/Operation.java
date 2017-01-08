package c.Enum_state_machine;

/**
 * Created by islabukhin on 29.09.16.
 */
public enum Operation {
    PLUS   { double eval(double x, double y) { return x + y; } },
    MINUS  { double eval(double x, double y) { return x - y; } },
    TIMES  { double eval(double x, double y) { return x * y; } },
    DIVIDE { double eval(double x, double y) { return x / y; } };

    // Do arithmetic op represented by this constant
    abstract double eval(double x, double y);
}

class OperationRunner{
    public static void main(String[] args) {
        System.out.println(Operation.PLUS.eval(2,2));
    }
}