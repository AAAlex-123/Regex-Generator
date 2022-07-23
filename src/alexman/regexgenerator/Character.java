package alexman.regexgenerator;

import java.util.regex.Pattern;

public interface Character {

    static Construct STRING(String string) {
        return () -> string;
    }

    Construct BACKSLASH = () -> "\\\\";

    Construct TAB = () -> "\\t";

    Construct LINE_FEED = () -> "\\n";

    Construct CARRIAGE_RETURN = () -> "\\r";

    Construct FORM_FEED = () -> "\\f";

    Construct ALERT = () -> "\\a";

    Construct ESCAPE = () -> "\\e";

    static Construct CONTROL(char x) {
        return () -> "\\c" + x;
    }

    static Construct OCT(String octal) {
        return new Octal(octal);
    }

    static Construct HEX(String hex) {
        return new Hex(hex);
    }

    class Hex implements Construct {

        private static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-f]{2}|[0-9a-f]{4,}");

        private final String hexLiteral;

        public Hex(String hex) {
            if (!HEX_PATTERN.matcher(hex).matches())
                throw new MalformedConstructException("Invalid hex literal: " + hex);

            int hexValue = Integer.parseInt(hex, 16);
            if (!(java.lang.Character.MIN_CODE_POINT <= hexValue && hexValue <= java.lang.Character.MAX_CODE_POINT))
                throw new MalformedConstructException("Invalid hex literal: " + hex);

            this.hexLiteral = hex;
        }

        @Override
        public String toRegex() {
            return "\\x" + hexLiteral;
        }
    }

    class Octal implements Construct {

        private static final Pattern OCTAL_PATTERN = Pattern.compile("[0-7]{1,2}|[0-3][0-7]{2}");

        private final String octalLiteral;

        public Octal(String octal) {
            if (!OCTAL_PATTERN.matcher(octal).matches())
                throw new MalformedConstructException("Invalid octal literal: " + octal);

            this.octalLiteral = octal;
        }

        @Override
        public String toRegex() {
            return "\\0" + octalLiteral;
        }
    }
}
