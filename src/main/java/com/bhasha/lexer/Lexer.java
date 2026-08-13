package com.bhasha.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String source;

    private int position = 0;
    private int line = 1;
    private int column = 1;

    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("declare", TokenType.DECLARE);
        keywords.put("integer", TokenType.INTEGER);
        keywords.put("decimal", TokenType.DECIMAL);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("print", TokenType.PRINT);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {

        while (!isAtEnd()) {

            skipWhitespace();

            if (isAtEnd()) {
                break;
            }

            int startLine = line;
            int startColumn = column;

            char current = advance();

            // Identifier or keyword
            if (Character.isLetter(current) || current == '_') {

                String text = readIdentifier(current);

                TokenType type = keywords.get(text);

                if (type == null) {
                    type = TokenType.IDENTIFIER;
                }

                addToken(type, text, startLine, startColumn);
            }

            // Number
            else if (Character.isDigit(current)) {

                String number = readNumber(current);

                TokenType type =
                        number.contains(".")
                                ? TokenType.NUMBER
                                : TokenType.NUMBER;

                addToken(type, number, startLine, startColumn);
            }

            else {

                switch (current) {

                    case '+' -> addToken(
                            TokenType.PLUS,
                            "+",
                            startLine,
                            startColumn
                    );

                    case '-' -> addToken(
                            TokenType.MINUS,
                            "-",
                            startLine,
                            startColumn
                    );

                    case '*' -> addToken(
                            TokenType.MULTIPLY,
                            "*",
                            startLine,
                            startColumn
                    );

                    case '/' -> addToken(
                            TokenType.DIVIDE,
                            "/",
                            startLine,
                            startColumn
                    );

                    case '%' -> addToken(
                            TokenType.MODULO,
                            "%",
                            startLine,
                            startColumn
                    );

                    case '=' -> {

                        if (match('=')) {
                            addToken(
                                    TokenType.EQUAL,
                                    "==",
                                    startLine,
                                    startColumn
                            );
                        } else {
                            addToken(
                                    TokenType.ASSIGN,
                                    "=",
                                    startLine,
                                    startColumn
                            );
                        }
                    }

                    case '!' -> {

                        if (match('=')) {
                            addToken(
                                    TokenType.NOT_EQUAL,
                                    "!=",
                                    startLine,
                                    startColumn
                            );
                        } else {
                            addToken(
                                    TokenType.INVALID,
                                    "!",
                                    startLine,
                                    startColumn
                            );
                        }
                    }

                    case '<' -> {

                        if (match('=')) {
                            addToken(
                                    TokenType.LESS_EQUAL,
                                    "<=",
                                    startLine,
                                    startColumn
                            );
                        } else {
                            addToken(
                                    TokenType.LESS,
                                    "<",
                                    startLine,
                                    startColumn
                            );
                        }
                    }

                    case '>' -> {

                        if (match('=')) {
                            addToken(
                                    TokenType.GREATER_EQUAL,
                                    ">=",
                                    startLine,
                                    startColumn
                            );
                        } else {
                            addToken(
                                    TokenType.GREATER,
                                    ">",
                                    startLine,
                                    startColumn
                            );
                        }
                    }

                    case '(' -> addToken(
                            TokenType.LEFT_PAREN,
                            "(",
                            startLine,
                            startColumn
                    );

                    case ')' -> addToken(
                            TokenType.RIGHT_PAREN,
                            ")",
                            startLine,
                            startColumn
                    );

                    case '{' -> addToken(
                            TokenType.LEFT_BRACE,
                            "{",
                            startLine,
                            startColumn
                    );

                    case '}' -> addToken(
                            TokenType.RIGHT_BRACE,
                            "}",
                            startLine,
                            startColumn
                    );

                    case ';' -> addToken(
                            TokenType.SEMICOLON,
                            ";",
                            startLine,
                            startColumn
                    );

                    default -> addToken(
                            TokenType.INVALID,
                            String.valueOf(current),
                            startLine,
                            startColumn
                    );
                }
            }
        }

        tokens.add(
                new Token(
                        TokenType.END,
                        "",
                        line,
                        column
                )
        );

        return tokens;
    }

    private String readIdentifier(char firstCharacter) {

        StringBuilder result = new StringBuilder();

        result.append(firstCharacter);

        while (!isAtEnd()) {

            char current = peek();

            if (Character.isLetterOrDigit(current) || current == '_') {
                result.append(advance());
            } else {
                break;
            }
        }

        return result.toString();
    }

    private String readNumber(char firstCharacter) {

        StringBuilder result = new StringBuilder();

        result.append(firstCharacter);

        boolean hasDecimalPoint = false;

        while (!isAtEnd()) {

            char current = peek();

            if (Character.isDigit(current)) {
                result.append(advance());
            }

            else if (current == '.' && !hasDecimalPoint) {

                hasDecimalPoint = true;
                result.append(advance());
            }

            else {
                break;
            }
        }

        return result.toString();
    }

    private void skipWhitespace() {

        while (!isAtEnd()) {

            char current = peek();

            if (current == ' ' || current == '\t' || current == '\r') {
                advance();
            }

            else if (current == '\n') {
                advance();
                line++;
                column = 1;
            }

            else {
                break;
            }
        }
    }

    private boolean match(char expected) {

        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(position) != expected) {
            return false;
        }

        advance();

        return true;
    }

    private char advance() {

        char current = source.charAt(position);

        position++;
        column++;

        return current;
    }

    private char peek() {

        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(position);
    }

    private boolean isAtEnd() {
        return position >= source.length();
    }

    private void addToken(
            TokenType type,
            String lexeme,
            int tokenLine,
            int tokenColumn) {

        tokens.add(
                new Token(
                        type,
                        lexeme,
                        tokenLine,
                        tokenColumn
                )
        );
    }
}