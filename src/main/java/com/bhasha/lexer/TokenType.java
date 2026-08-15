package com.bhasha.lexer;

public enum TokenType {
    END,
    IDENTIFIER,
    NUMBER,

    DECLARE,
    INTEGER,
    DECIMAL,
    IF,
    ELSE,
    WHILE,
    PRINT,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,

    INVALID
}