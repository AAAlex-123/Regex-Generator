package alexman.regexgenerator;

public class CharacterClass implements Construct {

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
