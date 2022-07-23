package alexman.regexgenerator.main;

import static alexman.regexgenerator.Construct.*;
import static alexman.regexgenerator.Construct.CharacterClass.*;
import static alexman.regexgenerator.Construct.Type.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern url = Pattern.compile(
                line_start
                    .append(capture(
                            literal("http")
                                    .append(optional(literal("s"), greedy))))
                    .append(literal("://"))
                    .append(capture(
                            optional(named(exactly_n(literal("w"), 3, greedy), "subdomain"), greedy)
                                    .append(literal("."))
                                    .append(named(
                                            range('a', 'm').subtraction(range('c', 'f'))
                                                    .append(zero_or_more(word, greedy)), "second"))
                                    .append(raw("\\."))
                                    .append(named(or(literal("com"), literal("gr")), "top"))
                                    .append(optional(literal("/"), greedy))
                                    .append(optional(named(at_least_one(word, greedy), "subdir"), greedy))))
                    .append(line_end)
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
