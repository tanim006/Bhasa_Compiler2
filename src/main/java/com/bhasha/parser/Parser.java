package com.bhasha.parser;

import com.bhasha.ast.*;
import com.bhasha.lexer.Token;
import com.bhasha.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final List<String> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> errors() {
        return List.copyOf(errors);
    }

    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }
    private boolean atEnd() { return peek().type() == TokenType.END; }

    private boolean check(TokenType type) {
        return !atEnd() && peek().type() == type;
    }

    private Token advance() {
        if (!atEnd()) current++;
        return previous();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        error(peek(), message);
        return new Token(type, "", peek().line(), peek().column());
    }

    private void error(Token token, String message) {
        errors.add("Syntax error at " + token.line() + ":" + token.column() + ": " + message);
    }

    // Required basic error recovery: skip until semicolon or closing brace.
    private void synchronize() {
        while (!atEnd()) {
            if (previous().type() == TokenType.SEMICOLON) return;
            if (peek().type() == TokenType.RBRACE) return;
            advance();
        }
    }

    public List<Stmt> parse() {
        List<Stmt> program = new ArrayList<>();
        while (!atEnd()) {
            try {
                Stmt stmt = statement();
                if (stmt != null) program.add(stmt);
            } catch (RuntimeException ex) {
                error(peek(), "Unable to parse statement.");
                synchronize();
            }
        }
        return program;
    }

    private Stmt statement() {
        if (match(TokenType.KW_DECLARE)) return declaration();
        if (match(TokenType.KW_IF)) return ifStatement();
        if (match(TokenType.KW_WHILE)) return whileStatement();
        if (match(TokenType.KW_PRINT)) return printStatement();
        if (check(TokenType.IDENT)) return assignment();

        error(peek(), "Expected a statement.");
        synchronize();
        return null;
    }

    private Stmt declaration() {
        Type type;
        if (match(TokenType.KW_INTEGER)) type = Type.INTEGER;
        else if (match(TokenType.KW_DECIMAL)) type = Type.DECIMAL;
        else {
            error(peek(), "Expected 'সংখ্যা' or 'দশমিক'.");
            synchronize();
            return null;
        }

        Token name = consume(TokenType.IDENT, "Expected variable name.");
        consume(TokenType.ASSIGN, "Expected '=' after variable name.");
        Expr initializer = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after declaration.");
        return new DeclarationStmt(type, name.lexeme(), initializer);
    }

    private Stmt assignment() {
        Token name = consume(TokenType.IDENT, "Expected variable name.");
        consume(TokenType.ASSIGN, "Expected '=' in assignment.");
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after assignment.");
        return new AssignmentStmt(name.lexeme(), value);
    }

    private Stmt printStatement() {
        consume(TokenType.LPAREN, "Expected '(' after 'লেখ'.");
        Expr value = expression();
        consume(TokenType.RPAREN, "Expected ')' after expression.");
        consume(TokenType.SEMICOLON, "Expected ';' after print statement.");
        return new PrintStmt(value);
    }

    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Expected '(' after 'যদি'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expected ')' after condition.");
        consume(TokenType.LBRACE, "Expected '{' before if body.");
        BlockStmt thenBranch = block();

        BlockStmt elseBranch = null;
        if (match(TokenType.KW_ELSE)) {
            consume(TokenType.LBRACE, "Expected '{' before else body.");
            elseBranch = block();
        }

        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(TokenType.LPAREN, "Expected '(' after 'যতক্ষণ'.");
        Expr condition = expression();
        consume(TokenType.RPAREN, "Expected ')' after condition.");
        consume(TokenType.LBRACE, "Expected '{' before while body.");
        BlockStmt body = block();
        return new WhileStmt(condition, body);
    }

    private BlockStmt block() {
        List<Stmt> statements = new ArrayList<>();
        while (!atEnd() && !check(TokenType.RBRACE)) {
            Stmt stmt = statement();
            if (stmt != null) statements.add(stmt);
        }
        consume(TokenType.RBRACE, "Expected '}' after block.");
        return new BlockStmt(statements);
    }

    // Precedence: equality < comparison < term < factor < unary < primary.
    private Expr expression() { return equality(); }

    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.EQ, TokenType.NE)) {
            TokenType op = previous().type();
            expr = new BinaryExpr(expr, op, comparison());
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.LT, TokenType.LE, TokenType.GT, TokenType.GE)) {
            TokenType op = previous().type();
            expr = new BinaryExpr(expr, op, term());
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previous().type();
            expr = new BinaryExpr(expr, op, factor());
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            TokenType op = previous().type();
            expr = new BinaryExpr(expr, op, unary());
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenType.MINUS, TokenType.PLUS)) {
            return new UnaryExpr(previous().type(), unary());
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.NUMBER)) return new NumberExpr(previous().lexeme());
        if (match(TokenType.IDENT)) return new VariableExpr(previous().lexeme());

        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression.");
            return expr;
        }

        error(peek(), "Expected expression.");
        if (!atEnd()) advance();
        return new NumberExpr("0");
    }
}
