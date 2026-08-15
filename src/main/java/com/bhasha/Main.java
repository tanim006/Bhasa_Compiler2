package com.bhasha;

import com.bhasha.lexer.Lexer;
import com.bhasha.lexer.Token;
import com.bhasha.parser.Parser;
import com.bhasha.ast.Stmt;
import com.bhasha.semantic.SemanticChecker;

import java.util.List;

public class Main {

    public static void main(String[] args) {

    /*String source = """
        declare integer x = 10 / 0;
        """; */
    /*String source = """
        declare integer x = 10;
        print(y);
        """; */
    String source = """
        declare integer x = 10.5;
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

            // 3. Semantic analysis
            System.out.println("\n=== SEMANTIC ANALYSIS ===");

            SemanticChecker checker = new SemanticChecker();

            try {
                checker.check(statements);
                System.out.println("Semantic analysis successful!");
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

        } else {

            for (String error : parser.errors()) {
                System.out.println(error);
            }
        }
    }
}