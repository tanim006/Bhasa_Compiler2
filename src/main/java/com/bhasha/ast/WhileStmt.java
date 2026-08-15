package com.bhasha.ast;

public final class WhileStmt extends Stmt {
    private final Expr condition;
    private final BlockStmt body;

    public WhileStmt(Expr condition, BlockStmt body) {
        this.condition = condition;
        this.body = body;
    }

    public Expr condition() { return condition; }
    public BlockStmt body() { return body; }
}
