package alexman.regexgenerator;

public interface Quantifier {

    enum Type {
        GREEDY(""),

        RELUCTANT("?"),

        POSSESSIVE("+");

        private final String s;

        Type(String s) {
            this.s = s;
        }
    }

    static Construct OPTIONAL(Construct construct, Type type) {
        return () -> String.format("%s?%s", construct.toRegex(), type.s);
    }

    static Construct ZERO_OR_MORE(Construct construct, Type type) {
        return () -> String.format("%s*%s", construct.toRegex(), type.s);
    }

    static Construct AT_LEAST_ONE(Construct construct, Type type) {
        return () -> String.format("%s+%s", construct.toRegex(), type.s);
    }

    static Construct EXACTLY_N(Construct construct, int n, Type type) {
        return () -> String.format("%s{%d}%s", construct.toRegex(), n, type.s);
    }

    static Construct AT_LEAST_N(Construct construct, int n, Type type) {
        return () -> String.format("%s{%d,}%s", construct.toRegex(), n, type.s);
    }

    static Construct BETWEEN_N_AND_M(Construct construct, int n, int m, Type type) {
        return () -> String.format("%s{%d,%d}%s", construct.toRegex(), n, m, type.s);
    }
}
