package alexman.regexgenerator;

public interface LogicalOperator {

    static Construct OR(Construct first, Construct second) {
        return () -> first.toRegex() + "|" + second.toRegex();
    }

    static Construct CAPTURE(Construct c) {
        return () -> "(" + c.toRegex() + ")";
    }
}
