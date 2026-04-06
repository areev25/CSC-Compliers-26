package Typecheck.Pass;

import Typecheck.TypeCheckException;
import Typecheck.Types.*;
//this pass takes the text the user wrote and
// translates into its Types that will later be userd
public class TypeAnnotationPass extends ScopePass<Void> {

    // Hint: Build the base type from the name, then wrap it for pointers and any [] modifiers.
    // 1. Construct the base type ("string" -> STRING)
    // 2. Wrap the base type in a POINTER Type if stars count > 0
    // 3. If the Array has concrete values ([10][3][9]), Then construct a LIST
    //         a. Loop over the List of brackets ([x][i][j]...)
    //            For the first bracket ([x]) construct a List of "basetype"
    //                  LIST(basetype, basetype, basetype,... x times)
    //            For the next bracket ([i]) construct a List of the PREVIOUS List
    //                  LIST(
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     LIST(basetype, basetype, basetype,... x times),
    //                     ... i times
    //                  )
    //            Keep repeating until no more brackets
    // 4. If the Array does not have expressions ([][][]...), then construct an ARRAY
    //         a. Pull the first bracket ([]) and construct an ARRAY(basetype)
    //            Pull the next bracket and construct an ARRAY(ARRAY(basetype))
    //            Keep repeating.
    public TypeAnnotationPass(Scope s) {
        super(s);
    }
    @Override
    public Void visitType(Absyn.Type node) {
        // is the base identity of the type. Is it a primitive (int/string), 
        // a placeholder (ALIAS), or the absence of a type (VOID)?
        Type currentType = node.name.equals("int") ? new INT()
                        : node.name.equals("string") ? new STRING()
                        : node.name.equals("void") ? new VOID()
                        : new ALIAS(node.name);
        // For every 'star' symbol in the source code, wrap the current type 
        // inside a POINTER object. (e.g., int** becomes POINTER(POINTER(INT)))
        for (int i = 0; i < node.stars; i++) {
            currentType = new POINTER(currentType);
        }

        // 3. Structural Wrapping (Arrays and Lists):
        // Examine the brackets from the inside out. 
        // Empty brackets [] imply a dynamic ARRAY.
        // Brackets with a constant [10] imply a fixed-size LIST.
        for (Absyn.ArrayType bracket : node.brackets.list) {
            if (bracket.size instanceof Absyn.EmptyExp) {
                // No size specified: treat as a pointer-style ARRAY
                currentType = new ARRAY(currentType);
            } else if (bracket.size instanceof Absyn.DecLit) {
                // Concrete size found: treat as a rigid LIST of specific length
                int size = ((Absyn.DecLit) bracket.size).value;
                currentType = new LIST(currentType, size); 
            }
        }

        // 4. Final Stamp:
        // Attach this fully-constructed type "blueprint" back onto the AST node 
        // so the JudgementsPass can find it later.
        node.typeAnnotation = currentType;
        return null;
    }
    //let the complier know what types certain inputs are and
    //if these should be hardcoded
    @Override
    public Void visitDecLit(Absyn.DecLit node) {
        // Label raw numeric constants as the INT type
        node.typeAnnotation = new INT();
        return null;
    }
    //helps look up variables!
    @Override
    public Void visitStrLit(Absyn.StrLit node) {
        // Label text constants as the STRING type
        node.typeAnnotation = new STRING();
        return null;
    }
    @Override
    public Void visitIdExp(Absyn.IdExp node) {
        // Retrieve the type definition from the Symbol Table (the "Scope")
        // This connects this usage of a variable to its earlier declaration.
        Type discoveredType = currentscope.getVar(node.name);
        
        if (discoveredType == null) {
            throw new TypeCheckException("The variable '" + node.name + "' has not been defined.");
        }
        
        // Stamp the discovered type onto this specific expression node
        node.typeAnnotation = discoveredType;
        return null;
    }
}
