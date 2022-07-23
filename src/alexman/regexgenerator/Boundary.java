package alexman.regexgenerator;

public interface Boundary {

    Construct LINE_START = () -> "^";

    Construct LINE_END = () -> "$";

    Construct WORD_BOUNDARY = () -> "\\b";

    Construct NON_WORD_BOUNDARY = () -> "\\B";

    Construct INPUT_START = () -> "\\A";

    Construct END_OF_PREV_MATCH = () -> "\\G";

    Construct INPUT_END_FOR_FINAL_TERMINATOR = () -> "\\Z";

    Construct INPUT_END = () -> "\\z";

}
