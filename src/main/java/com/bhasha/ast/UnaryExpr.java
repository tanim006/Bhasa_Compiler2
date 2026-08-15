package com.bhasha.ast;

import com.bhasha.lexer.TokenType;

public final class UnaryExpr extends Expr {
    private final TokenType operator;
    private final Expr right;

    public UnaryExpr(TokenType operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }

    public TokenType operator() { return operator; }
    public Expr right() { return right; }
}
