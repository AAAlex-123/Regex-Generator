package alexman.regexgenerator;

import java.util.regex.Pattern;

public interface Construct {

    String toRegex();

    // CHARACTERS

    static Construct raw(String rawString) {
        return () -> rawString;
    }

    Construct backslash = () -> "\\\\";

    static Construct oct(String octal) {
        if (!Pattern.matches("[0-7]{1,2}|[0-3][0-7]{2}", octal))
            throw new MalformedConstructException("Invalid octal literal: " + octal);

        return () -> "\\0" + octal;
    }

    static Construct hex(String hex) {
        if (!Pattern.matches("[0-9a-f]{2}|[0-9a-f]{4,}", hex))
            throw new MalformedConstructException("Invalid hex literal: " + hex);

        int hexValue = Integer.parseInt(hex, 16);
        if (!(java.lang.Character.MIN_CODE_POINT <= hexValue && hexValue <= java.lang.Character.MAX_CODE_POINT))
            throw new MalformedConstructException("Invalid hex literal: " + hex);

        return () -> "\\x" + hex;
    }

    Construct tab = () -> "\\t";

    Construct line_feed = () -> "\\n";

    Construct carriage_return = () -> "\\r";

    Construct form_feed = () -> "\\f";

    Construct alert = () -> "\\a";

    Construct escape = () -> "\\e";

    static Construct control(char x) {
        return () -> "\\c" + x;
    }

    // BOUNDARIES

    Construct line_start = () -> "^";

    Construct line_end = () -> "$";

    Construct word_boundary = () -> "\\b";

    Construct non_word_boundary = () -> "\\B";

    Construct input_start = () -> "\\A";

    Construct end_of_prev_match = () -> "\\G";

    Construct input_end_for_final_terminator = () -> "\\Z";

    Construct input_end = () -> "\\z";

    // QUANTIFIERS

    enum Type {
        greedy(""),

        reluctant("?"),

        possessive("+");

        private final String s;

        Type(String s) {
            this.s = s;
        }
    }

    static Construct optional(Construct construct) {
        return optional(construct, Type.greedy);
    }

    static Construct zero_or_more(Construct construct) {
        return zero_or_more(construct, Type.greedy);
    }

    static Construct at_least_one(Construct construct) {
        return at_least_one(construct, Type.greedy);
    }

    static Construct exactly_n(int n, Construct construct) {
        return exactly_n(n, Type.greedy, construct);
    }

    static Construct at_least_n(int n, Construct construct) {
        return at_least_n(n, Type.greedy, construct);
    }

    static Construct between_n_and_m(int n, int m, Construct construct) {
        return between_n_and_m(n, m, Type.greedy, construct);
    }

    static Construct optional(Construct construct, Type type) {
        return () -> String.format("%s?%s", construct.toRegex(), type.s);
    }

    static Construct zero_or_more(Construct construct, Type type) {
        return () -> String.format("%s*%s", construct.toRegex(), type.s);
    }

    static Construct at_least_one(Construct construct, Type type) {
        return () -> String.format("%s+%s", construct.toRegex(), type.s);
    }

    static Construct exactly_n(int n, Type type, Construct construct) {
        return () -> String.format("%s{%d}%s", construct.toRegex(), n, type.s);
    }

    static Construct at_least_n(int n, Type type, Construct construct) {
        return () -> String.format("%s{%d,}%s", construct.toRegex(), n, type.s);
    }

    static Construct between_n_and_m(int n, int m, Type type, Construct construct) {
        return () -> String.format("%s{%d,%d}%s", construct.toRegex(), n, m, type.s);
    }

    // LOGICAL OPERATORS

    default Construct append(Construct other) {
        return () -> this.toRegex() + other.toRegex();
    }

    default Construct or(Construct other) {
        return () -> this.toRegex() + "|" + other.toRegex();
    }

    static Construct capture(Construct c) {
        return () -> "(" + c.toRegex() + ")";
    }

    // BACK REFERENCES

    static Construct n_th_group(int n) {
        return () -> "\\" + n;
    }

    static Construct named_group(String name) {
        return () -> "\\k<" + name + ">";
    }

    // QUOTES

    static Construct literal(String string) {
        return () -> Pattern.quote(string);
    }

    // GROUPS

    static Construct named_group(String name, Construct c) {
        return () -> String.format("(?<%s>%s)", name, c.toRegex());
    }

    static Construct non_capturing(Construct c) {
        return () -> String.format("(?:%s)", c.toRegex());
    }

    static Construct positive_lookahead(Construct c) {
        return () -> String.format("(?=%s)", c.toRegex());
    }

    static Construct negative_lookahead(Construct c) {
        return () -> String.format("(?!%s)", c.toRegex());
    }

    static Construct positive_lookbehind(Construct c) {
        return () -> String.format("(?<=%s)", c.toRegex());
    }

    static Construct negative_lookbehind(Construct c) {
        return () -> String.format("(?<!%s)", c.toRegex());
    }

    static Construct independent_non_capturing(Construct c) {
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

        public static CharacterClass negate(CharacterClass other) {
            return of("^" + other.chars);
        }

        public static CharacterClass range(char from, char to) {
            return of(String.format("%c-%c", from, to));
        }

        public static CharacterClass range(char from1, char to1, char from2, char to2) {
            return of(String.format("%c-%c%c-%c", from1, to1, from2, to2));
        }

        public static CharacterClass range(char from1, char to1, char from2, char to2, char from3, char to3) {
            return of(String.format("%c-%c%c-%c%c-%c", from1, to1, from2, to2, from3, to3));
        }

        public CharacterClass union(CharacterClass other) {
            return of(this.chars + other.chars);
        }

        public CharacterClass intersect(CharacterClass other) {
            return of(this.chars + "&&" + other.toRegex());
        }

        public CharacterClass subtract(CharacterClass other) {
            return of(this.chars + "&&" + negate(other).toRegex());
        }

        public static Construct any = () -> ".";

        // public static final CharacterClass digit = of("[0-9]");
        public static final CharacterClass digit = of("\\d");

        // public static final CharacterClass non_digit = negation(DIGIT);
        public static final CharacterClass non_digit = of("\\D");

        // public static final CharacterClass horizontal_whitespace = of(" \\t\\xA0\\u1680\\u180e"
                                                                         // + "\\u2000-\\u200a\\u202f\\u205f\\u3000");
        public static final CharacterClass horizontal_whitespace = of("\\h");

        // public static final CharacterClass non_horizontal_whitespace = negation(horizontal_whitespace);
        public static final CharacterClass non_horizontal_whitespace = of("\\H");

        // public static final CharacterClass whitespace = of(" \\t\\n\\x0B\\f\\r");
        public static final CharacterClass whitespace = of("\\s");

        // public static final CharacterClass non_whitespace = negation(whitespace);
        public static final CharacterClass non_whitespace = of("\\S");

        // public static final CharacterClass vertical_whitespace = of("\\n\\x0B\\f\\r\\x85\\u2028"
                                                                       // + "\\u2029");
        public static final CharacterClass vertical_whitespace = of("\\v");

        // public static final CharacterClass non_vertical_whitespace = negation(vertical_whitespace);
        public static final CharacterClass non_vertical_whitespace = of("\\V");

        // public static final CharacterClass word = of("a-z");
        public static final CharacterClass word = of("\\w");

        // public static final CharacterClass non_word = negation(word);
        public static final CharacterClass non_word = of("\\W");

    }
}
