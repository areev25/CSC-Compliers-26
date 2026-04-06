package Typecheck.Pass;
import Absyn.*;
import Typecheck.SymbolTable.*;
import Typecheck.TypeCheckException;
import Typecheck.Types.*;

//This file uses the annotations created in previous passes
// to judge if the code is logically valid.
public class JudgementsPass extends ScopePass<Void> {

    private Type expectedReturn; // Tracks the return type of the current function
    public JudgementsPass(Scope s) {
        super(s);
    }
    // =========================
    // Variable Declaration
    // =========================
    @Override
    public Void visitVarDecl(VarDecl node) {
        if (node.init != null) {
            // 1. Visit the initializer to ensure it's checked
            visit(node.init);

            // 2. Rule: Initializer type must be compatible with the variable type
            if (!node.typeAnnotation.canAccept(node.init.typeAnnotation)) {
                throw new TypeCheckException("Type Mismatch: Cannot initialize " + node.name + 
                    " of type " + node.typeAnnotation + " with " + node.init.typeAnnotation);
            }
        }
        return null;
    }

    // =========================
    // Binary Expressions (Math/Logic)
    // =========================
    @Override
    public Void visitBinOp(BinOp node) {
        super.visitBinOp(node);

        // Rule 1: Arithmetic requires numeric types (INT or POINTER)
        boolean leftOk = node.left.typeAnnotation instanceof INT || node.left.typeAnnotation instanceof POINTER;
        boolean rightOk = node.right.typeAnnotation instanceof INT || node.right.typeAnnotation instanceof POINTER;

        if (!leftOk || !rightOk) {
            throw new TypeCheckException("Arithmetic operations require numeric types (int or pointer)");
        }
        return null;
    }

    // =========================
    // Assignment
    // =========================
    @Override
    public Void visitAssignExp(AssignExp node) {
        super.visitAssignExp(node);

        // Rule: The left-hand side must be able to "accept" the right-hand side value
        if (!node.left.typeAnnotation.canAccept(node.right.typeAnnotation)) {
            throw new TypeCheckException("Assignment Mismatch: " + node.left.typeAnnotation + 
                " cannot accept " + node.right.typeAnnotation);
        }
        return null;
    }

    // =========================
    // Control Flow (If/While)
    // =========================
    @Override
    public Void visitIfStmt(IfStmt node) {
        super.visitIfStmt(node);

        // Rule 11: Conditions must evaluate to a number or a pointer
        Type condType = node.expression.typeAnnotation;
        if (!(condType instanceof INT || condType instanceof POINTER)) {
            throw new TypeCheckException("Condition must be numeric or a pointer");
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt node) {
        super.visitWhileStmt(node);

        // Rule 11: Same as IfStmt
        Type condType = node.expression.typeAnnotation;
        if (!(condType instanceof INT || condType instanceof POINTER)) {
            throw new TypeCheckException("While loop condition must be numeric or a pointer");
        }
        return null;
    }

    // =========================
    // Function Declarations & Returns
    // =========================
    @Override
    public Void visitFunDecl(FunDecl node) {
        // Save the outer return type (for nested functions)
        Type oldReturn = expectedReturn;
        
        // Extract return type from the function's own annotation
        if (node.typeAnnotation instanceof FUNCTION) {
            expectedReturn = ((FUNCTION) node.typeAnnotation).returnType;
        }

        super.visitFunDecl(node); // Process the body
        
        expectedReturn = oldReturn; // Restore previous state
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        super.visitReturnStmt(node);

        Type actualReturn = (node.expression != null) ? node.expression.typeAnnotation : new VOID();

        // Rule: Returned value must match the function's defined return type
        if (expectedReturn != null && !expectedReturn.canAccept(actualReturn)) {
            throw new TypeCheckException("Return Mismatch: Expected " + expectedReturn + 
                " but got " + actualReturn);
        }
        return null;
    }

    // =========================
    // Unary & Function Calls
    // =========================
    @Override
    public Void visitUnaryExp(UnaryExp node) {
        super.visitUnaryExp(node);
        if (!(node.exp.typeAnnotation instanceof INT)) {
            throw new TypeCheckException("Unary operators only apply to integers");
        }
        return null;
    }

    @Override
    public Void visitFunExp(FunExp node) {
        super.visitFunExp(node);
        // Additional parameter-to-argument matching logic would go here
        return null;
    }
}