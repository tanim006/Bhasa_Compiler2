package com.bhasha.ast;

public final class VariableExpr extends Expr {
    private final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    public String name() { return name; }
}
