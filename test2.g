var int* p = 100;    // Rule 1: Number assigned to pointer (Passes)
var int x = p;       // Rule 1a: Pointer assigned to int (Passes)
var int y = p + 5;   // Rule 1a: Pointer used in math (Passes)
var int k = "Hello World"; // Throws Error
//var string s = p;    // Rule 1: String assigned a number (Should FAIL)