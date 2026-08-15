package com.bhasha.semantic;

import com.bhasha.ast.*;
import com.bhasha.lexer.TokenType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SemanticChecker {

    private final Map<String, Type> variables = new HashMap<>();
    private final Set<String> assignedVariables = new HashSet<>();

    public void check(List<Stmt> statements) {

        for (Stmt statement : statements) {
            checkStatement(statement);
        }
    }

    // =========================
    // CHECK STATEMENTS
    // =========================

    private void checkStatement(Stmt statement) {

        // Variable declaration
        if (statement instanceof DeclarationStmt declaration) {

            String name = declaration.name();
            Type type = declaration.type();

            if (variables.containsKey(name)) {
                error("Variable '" + name + "' is already declared.");
            }

            variables.put(name, type);

            // Declaration with initializer
            if (declaration.initializer() != null) {

                Type initializerType =
                        checkExpression(declaration.initializer());

                if (initializerType != type) {
                    error(
                            "Type mismatch: variable '" +
                            name +
                            "' is " +
                            type +
                            " but received " +
                            initializerType
                    );
                }

                assignedVariables.add(name);
            }

            return;
        }

        // Assignment
        if (statement instanceof AssignmentStmt assignment) {

            String name = assignment.name();

            if (!variables.containsKey(name)) {
                error(
                        "Variable '" +
                        name +
                        "' is not declared."
                );
            }

            Type valueType =
                    checkExpression(assignment.value());

            Type variableType =
                    variables.get(name);

            if (valueType != variableType) {
                error(
                        "Type mismatch: cannot assign " +
                        valueType +
                        " to " +
                        variableType +
                        " variable '" +
                        name +
                        "'."
                );
            }

            assignedVariables.add(name);

            return;
        }

      if (statement instanceof PrintStmt print) {

    checkExpression(print.value());

    return;
}

        // If statement
        if (statement instanceof IfStmt ifStmt) {

            checkExpression(ifStmt.condition());

            checkStatement(ifStmt.thenBranch());

            if (ifStmt.elseBranch() != null) {
                checkStatement(ifStmt.elseBranch());
            }

            return;
        }

        // While statement
        if (statement instanceof WhileStmt whileStmt) {

            checkExpression(whileStmt.condition());

            checkStatement(whileStmt.body());

            return;
        }

        // Block statement
        if (statement instanceof BlockStmt block) {

            for (Stmt stmt : block.statements()) {
                checkStatement(stmt);
            }

            return;
        }

        error("Unknown statement.");
    }

    // =========================
    // CHECK EXPRESSIONS
    // =========================

    private Type checkExpression(Expr expression) {

        // Number
       if (expression instanceof NumberExpr number) {

    String value = number.value();

    if (value.contains(".")) {
        return Type.DECIMAL;
    }

    return Type.INTEGER;
}

        // Variable
        if (expression instanceof VariableExpr variable) {

            String name = variable.name();

            if (!variables.containsKey(name)) {
                error(
                        "Variable '" +
                        name +
                        "' is not declared."
                );
            }

            // Requirement 3:
            // Variable must be assigned before use
            if (!assignedVariables.contains(name)) {
                error(
                        "Variable '" +
                        name +
                        "' must be assigned before it is used."
                );
            }

            return variables.get(name);
        }

        // Binary expression
        if (expression instanceof BinaryExpr binary) {

            Type leftType =
                    checkExpression(binary.left());

            Type rightType =
                    checkExpression(binary.right());

            TokenType operator =
                    binary.operator();

            // Requirement 2:
            // Division by zero
            if (operator == TokenType.DIVIDE) {

                if (binary.right() instanceof NumberExpr number) {

                    if (isZero(number)) {

                        error(
                                "Division by zero is not allowed."
                        );
                    }
                }
            }

            // Type mismatch
            if (leftType != rightType) {

                error(
                        "Type mismatch in expression: " +
                        leftType +
                        " " +
                        operator +
                        " " +
                        rightType
                );
            }

            return leftType;
        }

        // Unary expression
        if (expression instanceof UnaryExpr unary) {

            return checkExpression(unary.right());
        }

        error("Unknown expression.");

        return null;
    }

    // =========================
    // DIVISION BY ZERO
    // =========================

    private boolean isZero(NumberExpr number) {

        return Double.parseDouble(number.value()) == 0.0;
    }

    // =========================
    // ERROR HANDLER
    // =========================

    private void error(String message) {

        throw new RuntimeException(
                "Semantic Error: " + message
        );
    }
}