package alexman.regexgenerator;

public interface Group {

    static Construct NAMED(Construct c, String name) {
        return () -> String.format("(?<%s>%s)", name, c.toRegex());
    }

    static Construct NON_CAPTURING(Construct c) {
        return () -> String.format("(?:%s)", c.toRegex());
    }

    static Construct POSITIVE_LOOKAHEAD(Construct c) {
        return () -> String.format("(?=%s)", c.toRegex());
    }

    static Construct NEGATIVE_LOOKAHEAD(Construct c) {
        return () -> String.format("(?!%s)", c.toRegex());
    }

    static Construct POSITIVE_LOOKBEHIND(Construct c) {
        return () -> String.format("(?<=%s)", c.toRegex());
    }

    static Construct NEGATIVE_LOOKBEHIND(Construct c) {
        return () -> String.format("(?<!%s)", c.toRegex());
    }

    static Construct INDEPENDENT_NON_CAPTURING(Construct c) {
        return () -> String.format("(?>%s)", c.toRegex());
    }
}
