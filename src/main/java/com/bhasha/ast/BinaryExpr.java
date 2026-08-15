package com.bhasha.ast;

import com.bhasha.lexer.TokenType;

public final class BinaryExpr extends Expr {
    private final Expr left;
    private final TokenType operator;
    private final Expr right;

    public BinaryExpr(Expr left, TokenType operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expr left() { return left; }
    public TokenType operator() { return operator; }
    public Expr right() { return right; }
}
