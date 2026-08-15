package com.bhasha.ast;

public final class NumberExpr extends Expr {
    private final String value;

    public NumberExpr(String value) {
        this.value = value;
    }

    public String value() { return value; }
}
