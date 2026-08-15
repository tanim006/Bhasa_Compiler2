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
// ------------------------- 
        // Variable Declaration 
        // ------------------------- 
        if (statement instanceof DeclarationStmt declaration) { 
 
            String name = declaration.name(); 
            Type type = declaration.type(); 
 
            // Check duplicate variable 
            if (variables.containsKey(name)) { 
                error("Variable '" + name + "' is already declared."); 
            } 
 
            variables.put(name, type); 
 
            // Check initializer if present 
            if (declaration.initializer() != null) { 
 
                Type initializerType = 
                        checkExpression(declaration.initializer()); 
 
                // Requirement 1: 
                // Type mismatch 
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
 
        // ------------------------- 
        // Assignment 
        // ------------------------- 
        if (statement instanceof AssignmentStmt assignment) { 
 
            String name = assignment.name(); 
 
            // Variable must be declared first 
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
 
            // Requirement 1: 
            // Type mismatch 
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
 
            // Assignment makes the variable assigned 
            assignedVariables.add(name); 
 
            return; 
        } 
 
        // ------------------------- 
        // Print Statement 
        // ------------------------- 
        if (statement instanceof Printstmt print) { 
 
            checkExpression(print.expression()); 
 
            return; 
        } 
 
        // ------------------------- 
        // If Statement 
        // ------------------------- 
        if (statement instanceof IfStmt ifStmt) { 
 
            checkExpression(ifStmt.condition()); 
 
            checkStatement(ifStmt.thenBranch()); 
 
            if (ifStmt.elseBranch() != null) { 
                checkStatement(ifStmt.elseBranch()); 
            } 
 
            return; 
        } 
 
        // ------------------------- 
        // While Statement 
        // ------------------------- 
        if (statement instanceof Whilestmt whileStmt) { 
 
            checkExpression(whileStmt.condition()); 
 
            checkStatement(whileStmt.body()); 
 
            return; 
        } 
 
        // ------------------------- 
        // Block Statement 
        // ------------------------- 
        if (statement instanceof Blockstmt block) { 
 
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
 
        // ------------------------- 
        // Number 
        // ------------------------- 
        if (expression instanceof NumberExpr number) { 
 
            return number.type(); 
        } 
 
        // ------------------------- 
        // Variable 
        // ------------------------- 
        if (expression instanceof VariableExpr variable) { 
 
            String name = variable.name(); 
 
            // Variable doesn't exist 
            if (!variables.containsKey(name)) { 
                error( 
                        "Variable '" + 
                        name + 
                        "' is not declared." 
                ); 
            } 
 
            // Requirement 3: 
            // Variable must be assigned 
            if (!assignedVariables.contains(name)) { 
                error( 
                        "Variable '" + 
                        name + 
                        "' must be assigned before it is used." 
                ); 
            } 
 
            return variables.get(name); 
        } 
 
        // ------------------------- 
        // Binary Expression 
        // ------------------------- 
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
 
            // Check types on both sides 
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
 
        // ------------------------- 
        // Unary Expression 
        // ------------------------- 
        if (expression instanceof UnaryExpr unary) { 
 
            return checkExpression(unary.expression()); 
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
