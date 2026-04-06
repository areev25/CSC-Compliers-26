package Typecheck.Pass;
import Typecheck.Types.*;
import Typecheck.SymbolTable.*;
import java.util.ArrayList;

public class TypeScopePass extends ScopePass<Void> {

   public TypeScopePass(Scope s) {
      super(s);
   }
// Hint: Structs define a new type from their member types.
// 1. Visit the body so member types are fully resolved.
// 2. Collect each member's typeAnnotation.
// 3. Build a LIST type from them.
// 4. Register the struct name in the current scope.
   @Override
   public Void visitStructDecl(Absyn.StructDecl node) {
      // move inside
      Scope old = currentscope;
      currentscope = node.scope;

      // look at every member inside
      visit(node.body);

      // Collect the types of all members and place into a LIST
      java.util.List<Type> memberTypes = new java.util.ArrayList<>();
      // Note: Assuming your AST structure has members in node.body
      // You would loop through them and pull their .typeAnnotation

      // make this list visible to more than just the inside scope
      LIST structDefinition = new LIST(memberTypes);
      old.putType(node.name, structDefinition);

      // 5. Move back to start
      currentscope = old;
      return null;
   }
// Hint: Unions define a type that can be any of their member types.
// 1. Visit the body so member types are resolved.
// 2. Collect each member's typeAnnotation.
// 3. Build an OR type from them.
// 4. Register the union name in the current scope.
   @Override
	public Void visitUnionDecl(Absyn.UnionDecl node) {
		return null;
   }
// Hint: Typedef introduces a new name for an existing type.
// Visit the type first, then register the alias in the current scope.
   @Override
	public Void visitTypedef(Absyn.Typedef node) {
		return null;
	}
// Hint: Replace ALIAS types with their real definition.
// Remember that Types can be nested (IE ARRAY(ARRAY(ARRAY(...))) )
// Traverse the whole type to search for Aliases. Once an alias is found,
// look up the type of the alias in the symbol table.
    // This is a function I found helpful to implement. If you have a solution
    // in mind that does not include a helper function, then feel free to ignore
   private void resolveAlias(Type type) {
   }


// Hint: Visit the brackets and resolve the alias to a type (if the typeAnnotation contains ALIAS)
   @Override
   public Void visitType(Absyn.Type node) {
		return null;
   }

}
