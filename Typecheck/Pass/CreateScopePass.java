package Typecheck.Pass;

import Absyn.*;
import Typecheck.SymbolTable.*;
//This file goes through the AST and creates
// dedicated placeholders that the it can later go back to
public class CreateScopePass extends Pass<Void> {

    protected Scope currentscope;
    public Scope globalscope;

    public CreateScopePass() {
        this.globalscope = new Scope();
        this.currentscope = globalscope;
        //need to add a visitor method becuase if the first node
        // isn't in the global scope it will start with a null scope
    }
    //this connects the AST to the symbol Table
    @Override
    public Void visitCompStmt(CompStmt node) {
        // If this is the very first CompStmt (the whole file), 
        // it needs to know about the global scope!
        node.scope = currentscope; 
        
        // Default behavior from Pass.java will visit decl_list and stmt_list
        return super.visitCompStmt(node); 
    }
// Hint: Functions introduce a new nested scope.
// 1. Create a new Scope whose parent is the current scope.
// 2. Temporarily switch currentscope to this new scope.
// 3. Visit the function type, parameters, and body.
// 4. Store the resulting scope in node.scope.
// 5. Restore the previous scope.
    @Override
    public Void visitFunDecl(FunDecl node) {
        Scope old = currentscope;

        Scope newScope = new Scope(currentscope);
        currentscope = newScope;

        visit(node.type);
        visit(node.params);
        visit(node.body);

        node.scope = newScope;

        currentscope = old;
        return null;
    }

// Hint: Struct bodies are evaluated inside their own scope.
// 1. Create a new Scope whose parent is the current scope.
// 2. Switch currentscope to this new scope.
// 3. Visit the struct body.
// 4. Store this scope in node.scope.
// 5. Restore the previous scope.
    @Override
    public Void visitStructDecl(StructDecl node) {
        Scope old = currentscope;

        Scope newScope = new Scope(currentscope);
        currentscope = newScope;

        visit(node.body);

        node.scope = newScope;

        currentscope = old;
        return null;
    }

// Hint: Union bodies behave like structs for scoping.
// 1. Create a new Scope whose parent is the current scope.
// 2. Switch currentscope to the new scope.
// 3. Visit the union body.
// 4. Store this scope in node.scope.
// 5. Restore the previous scope.
    @Override
    public Void visitUnionDecl(UnionDecl node) {
        Scope old = currentscope;

        Scope newScope = new Scope(currentscope);
        currentscope = newScope;

        visit(node.body);

        node.scope = newScope;

        currentscope = old;
        return null;
    }

// Hint: If statements execute inside a fresh scope.
// 1. Create a new Scope whose parent is the current scope.
// 2. Switch currentscope to this new scope.
// 3. Visit the condition and both branches.
// 4. Save this scope in node.scope.
// 5. Restore the previous scope.
    @Override
    public Void visitIfStmt(IfStmt node) {
        Scope old = currentscope;

        Scope newScope = new Scope(currentscope);
        currentscope = newScope;

        visit(node.expression);
        visit(node.if_statement);
        if (node.else_statement != null) {
            visit(node.else_statement);
        }

        node.scope = newScope;

        currentscope = old;
        return null;
    }

// Hint: Loops also introduce a nested scope.
// 1. Create a new Scope whose parent is the current scope.
// 2. Switch currentscope to the new scope.
// 3. Visit the condition and loop body.
// 4. Store the scope in node.scope.
// 5. Restore the previous scope.
    @Override
    public Void visitWhileStmt(WhileStmt node) {
        Scope old = currentscope;

        Scope newScope = new Scope(currentscope);
        currentscope = newScope;

        visit(node.expression);
        visit(node.statement);

        node.scope = newScope;

        currentscope = old;
        return null;
    }
}
