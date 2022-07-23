package alexman.regexgenerator;

import java.util.regex.Pattern;

public interface Construct {

    String toRegex();

    // CHARACTERS

    static Construct RAW(String rawString) {
        return () -> rawString;
    }

    Construct BACKSLASH = () -> "\\\\";

    static Construct OCT(String octal) {
        if (!Pattern.matches("[0-7]{1,2}|[0-3][0-7]{2}", octal))
            throw new MalformedConstructException("Invalid octal literal: " + octal);

        return () -> "\\0" + octal;
    }

    static Construct HEX(String hex) {
        if (!Pattern.matches("[0-9a-f]{2}|[0-9a-f]{4,}", hex))
            throw new MalformedConstructException("Invalid hex literal: " + hex);

        int hexValue = Integer.parseInt(hex, 16);
        if (!(java.lang.Character.MIN_CODE_POINT <= hexValue && hexValue <= java.lang.Character.MAX_CODE_POINT))
            throw new MalformedConstructException("Invalid hex literal: " + hex);

        return () -> "\\x" + hex;
    }

    Construct TAB = () -> "\\t";

    Construct LINE_FEED = () -> "\\n";

    Construct CARRIAGE_RETURN = () -> "\\r";

    Construct FORM_FEED = () -> "\\f";

    Construct ALERT = () -> "\\a";

    Construct ESCAPE = () -> "\\e";

    static Construct CONTROL(char x) {
        return () -> "\\c" + x;
    }

    // BOUNDARIES

    Construct LINE_START = () -> "^";

    Construct LINE_END = () -> "$";

    Construct WORD_BOUNDARY = () -> "\\b";

    Construct NON_WORD_BOUNDARY = () -> "\\B";

    Construct INPUT_START = () -> "\\A";

    Construct END_OF_PREV_MATCH = () -> "\\G";

    Construct INPUT_END_FOR_FINAL_TERMINATOR = () -> "\\Z";

    Construct INPUT_END = () -> "\\z";

    // QUANTIFIERS

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

    // LOGICAL OPERATORS

    default Construct APPEND(Construct other) {
        return () -> this.toRegex() + other.toRegex();
    }

    static Construct OR(Construct first, Construct second) {
        return () -> first.toRegex() + "|" + second.toRegex();
    }

    static Construct CAPTURE(Construct c) {
        return () -> "(" + c.toRegex() + ")";
    }

    // BACK REFERENCES

    static Construct N_TH_GROUP(int n) {
        return () -> "\\" + n;
    }

    static Construct NAMED_GROUP(String name) {
        return () -> "\\k<" + name + ">";
    }

    // QUOTES

    static Construct LITERAL(String string) {
        return () -> Pattern.quote(string);
    }

    // GROUPS

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

    class CharacterClass implements Construct {

        private final String chars;

        private CharacterClass(String chars) {
            this.chars = chars;
        }

        @Override
        public String toRegex() {
            return "[" + chars + "]";
            // return chars;
        }

        public static CharacterClass of(String chars) {
            return new CharacterClass(chars);
            // return new CharacterClass("[" + chars + "]");
        }

        public static CharacterClass NEGATION(CharacterClass other) {
            return of("^" + other.chars);
        }

        public static CharacterClass RANGE(char from, char to) {
            return of(String.format("%c-%c", from, to));
        }

        public static CharacterClass RANGE(char from1, char to1, char from2, char to2) {
            return of(String.format("%c-%c%c-%c", from1, to1, from2, to2));
        }

        public static CharacterClass RANGE(char from1, char to1, char from2, char to2, char from3, char to3) {
            return of(String.format("%c-%c%c-%c%c-%c", from1, to1, from2, to2, from3, to3));
        }

        public CharacterClass UNION(CharacterClass other) {
            return of(this.chars + other.chars);
        }

        public CharacterClass INTERSECTION(CharacterClass other) {
            return of(this.chars + "&&" + other.toRegex());
        }

        public CharacterClass SUBTRACTION(CharacterClass other) {
            return of(this.chars + "&&" + NEGATION(other).toRegex());
        }

        public static Construct ANY = () -> ".";

        // public static final CharacterClass DIGIT = of("[0-9]");
        public static final CharacterClass DIGIT = of("\\d");

        // public static final CharacterClass NON_DIGIT = NEGATION(DIGIT);
        public static final CharacterClass NON_DIGIT = of("\\D");

        // public static final CharacterClass HORIZONTAL_WHITESPACE = of(" \\t\\xA0\\u1680\\u180e"
                                                                         // + "\\u2000-\\u200a\\u202f\\u205f\\u3000");
        public static final CharacterClass HORIZONTAL_WHITESPACE = of("\\h");

        // public static final CharacterClass NON_HORIZONTAL_WHITESPACE = NEGATION(HORIZONTAL_WHITESPACE);
        public static final CharacterClass NON_HORIZONTAL_WHITESPACE = of("\\H");

        // public static final CharacterClass WHITESPACE = of(" \\t\\n\\x0B\\f\\r");
        public static final CharacterClass WHITESPACE = of("\\s");

        // public static final CharacterClass NON_WHITESPACE = NEGATION(WHITESPACE);
        public static final CharacterClass NON_WHITESPACE = of("\\S");

        // public static final CharacterClass VERTICAL_WHITESPACE = of("\\n\\x0B\\f\\r\\x85\\u2028"
                                                                       // + "\\u2029");
        public static final CharacterClass VERTICAL_WHITESPACE = of("\\v");

        // public static final CharacterClass NON_VERTICAL_WHITESPACE = NEGATION(VERTICAL_WHITESPACE);
        public static final CharacterClass NON_VERTICAL_WHITESPACE = of("\\V");

        // public static final CharacterClass WORD = of("a-z");
        public static final CharacterClass WORD = of("\\w");

        // public static final CharacterClass NON_WORD = NEGATION(WORD);
        public static final CharacterClass NON_WORD = of("\\W");

    }
}
