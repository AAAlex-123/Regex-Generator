package alexman.regexgenerator.main;

import static alexman.regexgenerator.CharacterClass.*;
import static alexman.regexgenerator.BackReference.*;
import static alexman.regexgenerator.Boundary.*;
import static alexman.regexgenerator.Character.*;
import static alexman.regexgenerator.Group.*;
import static alexman.regexgenerator.LogicalOperator.*;
import static alexman.regexgenerator.Quantifier.*;
import static alexman.regexgenerator.Quantifier.Type.*;
import static alexman.regexgenerator.Quotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern url = Pattern.compile(
                LINE_START
                    .APPEND(CAPTURE(
                            STRING("http")
                                    .APPEND(OPTIONAL(STRING("s"), GREEDY))))
                    .APPEND(STRING("://"))
                    .APPEND(CAPTURE(
                            OPTIONAL(NAMED(EXACTLY_N(STRING("w"), 3, GREEDY), "subdomain"), GREEDY)
                                    .APPEND(QUOTE)
                                    .APPEND(STRING("."))
                                    .APPEND(NAMED(
                                            RANGE('a', 'm').SUBTRACTION(RANGE('c', 'f'))
                                                    .APPEND(ZERO_OR_MORE(WORD, GREEDY)), "second"))
                                    .APPEND(QUOTE)
                                    .APPEND(STRING("."))
                                    .APPEND(NAMED(OR(STRING("com"), STRING("gr")), "top"))
                                    .APPEND(OPTIONAL(STRING("/"), GREEDY))
                                    .APPEND(OPTIONAL(NAMED(AT_LEAST_ONE(WORD, GREEDY), "subdir"), GREEDY))))
                    .APPEND(LINE_END)
                    .toRegex());

        System.out.println("Pattern: " + url);

        match(url, "https://www.google.com/gmail");
        match(url, "http://www.medium.com");
        match(url, "https://twitter.com/home");
    }

    private static void match(Pattern pattern, String input) {
        System.out.println("\n\nMatching: " + input);
        Matcher m = pattern.matcher(input);
        if (!m.matches()) {
            System.out.println("input doesn't match: " + input);
        } else {
            System.out.printf("schema:              %s%n", m.group(1));
            System.out.printf("rest:                %s%n", m.group(2));
            System.out.printf("subdomain:           %s%n", m.group("subdomain"));
            System.out.printf("second-level domain: %s%n", m.group("second"));
            System.out.printf("top-level domain:    %s%n", m.group("top"));
            System.out.printf("subdirectory:        %s%n", m.group("subdir"));
        }
    }
}
