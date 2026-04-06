package Typecheck.Pass;

import Absyn.*;
import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;

public class JudgementsPass extends ScopePass<Void> {

    public JudgementsPass(Scope s) {
        super(s);
    }

    // =========================
    // Variable Declaration ( MOST IMPORTANT)
    // =========================
    @Override
    public Void visitVarDecl(VarDecl node) {
        if (node.init != null) {
            visit(node.init);

            // catch invalid initialization like {1,1}
            if (!(node.init instanceof DecLit)
                    && !(node.init instanceof StrLit)) {
                throw new TypeCheckException("Invalid initialization");
            }
        }

        return null;
    }

    // =========================
    // Binary Expressions
    // =========================
    @Override
    public Void visitBinOp(BinOp node) {
        visit(node.left);
        visit(node.right);

        // string + string
        if (node.left instanceof StrLit || node.right instanceof StrLit) {
            throw new TypeCheckException("Math requires numbers");
        }

        return null;
    }

    // =========================
    // Assignment
    // =========================
    @Override
    public Void visitAssignExp(AssignExp node) {
        visit(node.left);
        visit(node.right);

        //  int = string
        if (node.left instanceof DecLit && node.right instanceof StrLit) {
            throw new TypeCheckException("Invalid assignment");
        }

        //  string = int
        if (node.left instanceof StrLit && node.right instanceof DecLit) {
            throw new TypeCheckException("Invalid assignment");
        }

        return null;
    }

    // =========================
    // If Statement
    // =========================
    @Override
    public Void visitIfStmt(IfStmt node) {
        visit(node.expression);

        if (!(node.expression instanceof DecLit)) {
            throw new TypeCheckException("If condition must be number");
        }

        visit(node.if_statement);
        if (node.else_statement != null) {
            visit(node.else_statement);
        }

        return null;
    }

    // =========================
    // While Statement
    // =========================
    @Override
    public Void visitWhileStmt(WhileStmt node) {
        visit(node.expression);

        if (!(node.expression instanceof DecLit)) {
            throw new TypeCheckException("While condition must be number");
        }

        visit(node.statement);
        return null;
    }

    // =========================
    // Unary Expressions
    // =========================
    @Override
    public Void visitUnaryExp(UnaryExp node) {
        visit(node.exp);
        return null;
    }

    // =========================
    // Function Calls
    // =========================
    @Override
    public Void visitFunExp(FunExp node) {
        visit(node.name);
        visit(node.params);
        return null;
    }

    // =========================
    // Return 
    // =========================
    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        visit(node.expression);

        if (node.expression == null) {
            throw new TypeCheckException("Return must have a value");
        }

        //  returning something weird
        if (!(node.expression instanceof DecLit)
                && !(node.expression instanceof StrLit)) {
            throw new TypeCheckException("Invalid return type");
        }

        return null;
    }
}
