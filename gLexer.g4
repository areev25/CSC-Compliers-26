
lexer grammar gLexer;

@header {
   package Parse.antlr_build;
}


@members {
   StringBuilder sb;
   private int stringToInt(String target) {
      if (target.startsWith("0x") || target.startsWith("0X")){
         return Integer.parseInt(target.substring(2), 16);
      }

      if (target.startsWith("0") && target.length() > 1) {
         return Integer.parseInt(target.substring(1), 8);
      }

      return Integer.parseInt(target, 10);
      }

      private boolean isHexDigit(int c) {
    return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
}

}


//Geaux Keywords
VAR : 'var';
FUN : 'fun';
WHILE : 'while';
CONST: 'const';
STRING : 'string';
VOID : 'void' ;
RETURN: 'return';
IF : 'if';
ELSE: 'else';
BREAK: 'break';
INT: 'int';
TYPEDEF: 'typedef';
STRUCT: 'struct';
UNION: 'union';

//Geaux operators:

LESS_THAN : '<';
AND : '&&';
OR : '||';
STAR : '*';
PLUS : '+';
EQ: '=';
DOT: '.';
ARROW : '->';
//GREATER_THAN : '>';

//Geaux punctuators:

LCURLY: '{';
RCURLY: '}';
COMMA : ',';
LPAREN : '(';
RPAREN : ')';
AMPERSAND : '&';
PIPE :  '|';
EXCLAMATION: '!';
TILDE : '~';
SEMI_COLON: ';';
COLON : ':';
LBRACK: '[';
RBRACK: ']';

//ID rule
ID 
   : [A-Za-z_] [A-Za-z0-9_]* 
   ;



DECIMAL_LITERAL
   :'0' [xX] [0-9a-fA-F]+ 
   | [1-9] DIGIT*
   ;


fragment ALPHA 
   : [A-Za-z]
   ;







OCTAL_LITERAL 
   : '0' [0-7]+
   ;


STRING_LITERAL
    : '"' ( ESC_SEQ | ~["\\\r\n] )* '"'
      {
        String raw = getText().substring(1, getText().length() - 1);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

            if (c == '\\') {
                char next = raw.charAt(++i);
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;

                    case 'x': {
                        String hex = "";
                        while (i + 1 < raw.length()
                               && Character.digit(raw.charAt(i + 1), 16) != -1) {
                            hex += raw.charAt(++i);
                        }
                        sb.append((char) Integer.parseInt(hex, 16));
                        break;
                    }

                    default:
                        if (next >= '0' && next <= '7') {
                            String oct = "" + next;
                            while (i + 1 < raw.length()
                                   && raw.charAt(i + 1) >= '0'
                                   && raw.charAt(i + 1) <= '7'
                                   && oct.length() < 3) {
                                oct += raw.charAt(++i);
                            }
                            sb.append((char) Integer.parseInt(oct, 8));
                        } else {
                            sb.append(next);
                        }
                }
            } else {
                sb.append(c);
            }
        }

        setText(sb.toString());
      }
    ;



fragment ESC_SEQ
    : '\\' .
    ;


//fragment ESC
  //  : '\\' [btnrf"\\]
   // | '\\' [0-7]{1,3}
   // | '\\x' HEX+
   // ;



fragment DIGIT

   : [0-9]
   ;





fragment HEX
   : [0-9a-fA-F]
   ;
LINE_COMMENT 
   : '//' ~[\r\n]* -> skip
   ;

BLOCK_COMMENT
   : '/*' .*? '*/' -> skip
   ;

WHITE_SPACE
   : [ \t\r\n]+ -> skip
   ;
