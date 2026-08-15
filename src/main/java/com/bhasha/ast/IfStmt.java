package com.bhasha.ast;

public final class IfStmt extends Stmt {
    private final Expr condition;
    private final BlockStmt thenBranch;
    private final BlockStmt elseBranch;

    public IfStmt(Expr condition, BlockStmt thenBranch, BlockStmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expr condition() { return condition; }
    public BlockStmt thenBranch() { return thenBranch; }
    public BlockStmt elseBranch() { return elseBranch; }
}
