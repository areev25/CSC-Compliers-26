package Typecheck.Pass;

import Absyn.*;
import Typecheck.SymbolTable.*;

public class ScopePass<T> extends Pass<T> {

    protected Scope currentscope;
    protected T defaultReturn = null;

    // Hint: Save scope → switch to node.scope → visit children → restore scope.
    public ScopePass(Scope s) {
        this.currentscope = s;
    }

    @Override
    public T visitFunDecl(FunDecl node) {
        Scope old = currentscope;
        currentscope = node.scope;

        visit(node.type);
        visit(node.params);
        visit(node.body);

        currentscope = old;
        return null;
    }

    @Override
    public T visitStructDecl(StructDecl node) {
        Scope old = currentscope;
        currentscope = node.scope;

        visit(node.body);

        currentscope = old;
        return null;
    }

    @Override
    public T visitUnionDecl(UnionDecl node) {
        Scope old = currentscope;
        currentscope = node.scope;

        visit(node.body);

        currentscope = old;
        return null;
    }

    @Override
    public T visitIfStmt(IfStmt node) {
        Scope old = currentscope;
        currentscope = node.scope;

        visit(node.expression);
        visit(node.if_statement);
        if (node.else_statement != null) {
            visit(node.else_statement);
        }

        currentscope = old;
        return null;
    }

    @Override
    public T visitWhileStmt(WhileStmt node) {
        Scope old = currentscope;
        currentscope = node.scope;

        visit(node.expression);
        visit(node.statement);

        currentscope = old;
        return null;
    }
}
