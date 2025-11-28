package Lexico;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String input;
    private int currentPosition;

    public Lexer(String input) {
        this.input = input;
        this.currentPosition = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentPosition < input.length()) {
            char currentChar = input.charAt(currentPosition);

            if (Character.isWhitespace(currentChar)) {
                currentPosition++;
                continue;
            }

            Token token = nextToken();
            if (token != null) {
                tokens.add(token);
            } else {
                throw new RuntimeException("Unknown character: " + currentChar);
            }
        }

        return tokens;
    }

    private Token nextToken() {
        if (currentPosition >= input.length()) {
            return null;
        }

        String[] tokenPatterns = {
            "if|else|while|for|String|int|boolean|True|False",         // Keywords
            "[a-zA-Z_][a-zA-Z0-9_]*",    // Identifiers
            "\\d+",                      // Literals
            "[+-/*=<>!]",                // Operators
            "[.,;(){}]",                 // Punctuation
        };

        TokenType[] tokenTypes = {
            TokenType.KEYWORD,
            TokenType.IDENTIFIER,
            TokenType.LITERAL,
            TokenType.OPERATOR,
            TokenType.PUNCTUATION,
        };

        for (int i = 0; i < tokenPatterns.length; i++) {
            Pattern pattern = Pattern.compile("^" + tokenPatterns[i]);
            Matcher matcher = pattern.matcher(input.substring(currentPosition));

            if (matcher.find()) {
                String value = matcher.group();
                currentPosition += value.length();
                return new Token(tokenTypes[i], value);
            }
        }

        return null;
    }

}
