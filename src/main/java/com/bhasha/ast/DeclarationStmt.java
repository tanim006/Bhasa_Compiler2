package com.bhasha.ast;

public final class DeclarationStmt extends Stmt {
    private final Type type;
    private final String name;
    private final Expr initializer;

    public DeclarationStmt(Type type, String name, Expr initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    public Type type() { return type; }
    public String name() { return name; }
    public Expr initializer() { return initializer; }
}
