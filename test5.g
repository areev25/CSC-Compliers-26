/*fun int main() {
    var string s = "hi";
    if (s) { return 1; } // Rule 11: Should FAIL (string in if)
    
    var int x = 1 + s;   // Rule 6: Should FAIL (math with string)
    return 0;
}
*/
fun int main() {
    var string s = "hi";
    var int x = 0; // Move this up here
    
    if (s) { return 1; } // This should trigger the "must be integer" error
    
    x = 1 + s; 
    return 0;
}