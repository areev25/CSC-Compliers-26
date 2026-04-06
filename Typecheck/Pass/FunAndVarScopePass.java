package Typecheck.Pass;

import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;
//In this file we are  fill in the variables names and functions
public class FunAndVarScopePass extends ScopePass<Void> {

    public FunAndVarScopePass(Scope s) {
        super(s);
    }
// Hint: Parameters behave like variables inside the function scope.
// 1. Ensure no function with this name already exists in the current scope.
// 2. Add the parameter as a variable symbol.
// 3. Use the parameter's typeAnnotation as its type.

    @Override
    public Void visitParameter(Absyn.Parameter node) {

        // Here is some code I used. You might find it useful:
        if (this.currentscope.hasLocalFun(node.name)) {
            throw new TypeCheckException("Tried to define var (" + node.name + ") but fun with same name already exists");
        }
        // Inside visitVarDecl
        this.currentscope.putVar(node.name, node.typeAnnotation);
        return null;
    }
// Hint: Functions must be registered in the current scope before visiting their body.
// 1. Ensure no variable with the same name exists in the current scope.
// 2. Collect the types of all parameters.
// 3. Construct the function type (parameter types → return type).
// 4. Add the function symbol to the current scope.
// 5. Enter the function’s scope and visit its contents.

    @Override
    public Void visitFunDecl(Absyn.FunDecl node) {
        // 1. We are currently in the OUTER scope (e.g., Global). 
        // Check if a variable already stole this name.
        if (this.currentscope.hasLocalVar(node.name)) {
            throw new TypeCheckException("Var " + node.name + " exists.");
        }

        // 2. Register the function in the OUTER scope so other functions can call it.
        this.currentscope.putFun(node.name, node.typeAnnotation);

        // 3. NOW move inside the function's private scope to visit params/body.
        return super.visitFunDecl(node); 
    }
// Hint: Struct members are variables within the struct's scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the member as a variable symbol using its annotated type.

    @Override
    public Void visitStructMember(Absyn.StructMember node) {
        // Just add the member to the current (struct) scope
        this.currentscope.putVar(node.name, node.typeAnnotation);
        return null;
    }
     
// Hint: Union members behave like variables within the union scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the member as a variable symbol using its annotated type.

    // Note: This is only true for now. Union's will get special treatement
    // later, but for now we treat them as the same as structs. 
    @Override
    public Void visitUnionMember(Absyn.UnionMember node) {
        // Just add the member to the current (struct) scope
        this.currentscope.putVar(node.name, node.typeAnnotation);
        return null;
    }
// Hint: Variable declarations introduce a new variable in the current scope.
// 1. Ensure no function with this name exists in the current scope.
// 2. Add the variable symbol using its annotated type.
    @Override
    public Void visitVarDecl(Absyn.VarDecl node) {
        // Safety check: Is there a function with this name?
        if (this.currentscope.hasLocalFun(node.name)) {
            throw new TypeCheckException("Cannot name var " + node.name + "; function exists.");
        }
        
        this.currentscope.putVar(node.name, node.typeAnnotation);
        return null;
    }

}
