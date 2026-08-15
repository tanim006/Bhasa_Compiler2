package com.bhasha.ast;

import java.util.List;

public final class BlockStmt extends Stmt {
    private final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    public List<Stmt> statements() { return statements; }
}
