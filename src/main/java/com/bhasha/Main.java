package com.bhasha;

import com.bhasha.lexer.Lexer;
import com.bhasha.lexer.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source = """
                declare integer x;
                x = 10 + 20;
                print(x);
                """;

        Lexer lexer = new Lexer(source);

        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}