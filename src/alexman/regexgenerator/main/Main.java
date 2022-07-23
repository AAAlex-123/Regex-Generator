package alexman.regexgenerator.main;

import static alexman.regexgenerator.Construct.*;
import static alexman.regexgenerator.Construct.CharacterClass.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Pattern url = Pattern.compile(
                line_start
                    .append(capture(
                            raw("http")
                                    .append(optional(raw("s")))))
                    .append(raw("://"))
                    .append(capture(
                            optional(named_group("subdomain", exactly_n(3, raw("w"))))
                                    .append(literal("."))
                                    .append(named_group("second",
                                            range('a', 'm').subtract(range('c', 'f'))
                                                       .append(zero_or_more(word))))
                                    .append(raw("\\."))
                                    .append(named_group("top", raw("com").or(raw("gr"))))
                                    .append(optional(raw("/")))
                                    .append(optional(
                                            named_group("subdir", at_least_one(word))))))
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
