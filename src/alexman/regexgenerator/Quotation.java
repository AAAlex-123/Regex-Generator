package alexman.regexgenerator;

public interface Quotation {

    Construct QUOTE = () -> "\\";

    Construct QUOTE_START = () -> "\\Q";

    Construct QUOTE_END = () -> "\\E";
}
