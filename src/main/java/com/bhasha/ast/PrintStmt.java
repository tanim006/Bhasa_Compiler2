package com.bhasha.ast;

public final class PrintStmt extends Stmt {
    private final Expr value;

    public PrintStmt(Expr value) {
        this.value = value;
    }

    public Expr value() { return value; }
}
