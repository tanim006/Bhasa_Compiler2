package com.bhasha.ast;

public final class AssignmentStmt extends Stmt {
    private final String name;
    private final Expr value;

    public AssignmentStmt(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    public String name() { return name; }
    public Expr value() { return value; }
}
