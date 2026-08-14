package com.bhasha;

import com.bhasha.lexer.Lexer;
import com.bhasha.lexer.Token;
import com.bhasha.parser.Parser;
import com.bhasha.ast.Stmt;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source = """
                declare integer x = 10;
                x = 10 + 20;
                print(x);
                """;

        // 1. Lexical analysis
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        System.out.println("=== TOKENS ===");

        for (Token token : tokens) {
            System.out.println(token);
        }

        // 2. Parsing
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        System.out.println("\n=== PARSER ===");

        if (parser.errors().isEmpty()) {
            System.out.println("Parsing successful!");
            System.out.println("Statements parsed: " + statements.size());
        } else {
            for (String error : parser.errors()) {
                System.out.println(error);
            }
        }
    }
}