package pers.oj;

public class Main {

    public static void main(String[] args) {
        // The initialization of lexical-analyzer generator
        // Parser.addParser(
        //      "Regular Expression",
        //      "The name of action function，which is written in 'Tools'")
        // The order of the function represents the priority
        Parser.addParser("if", "addKeyword");
        Parser.addParser("else", "addKeyword");
        Parser.addParser("then", "addKeyword");
        Parser.addParser("while", "addKeyword");
        Parser.addParser("do", "addKeyword");
        Parser.addParser("read", "addKeyword");
        Parser.addParser("write", "addKeyword");
        Parser.addParser("call", "addKeyword");
        Parser.addParser("begin", "addKeyword");
        Parser.addParser("end", "addKeyword");
        Parser.addParser("const", "addKeyword");
        Parser.addParser("var", "addKeyword");
        Parser.addParser("procedure", "addKeyword");
        Parser.addParser("odd", "addKeyword");


        Parser.addParser("([a-zA-Z]+[a-zA-Z0-9]*)", "addIndent");

        Parser.addParser(",", "addSymbol");
        Parser.addParser(";", "addSymbol");
        Parser.addParser(".", "addSymbol");

        Parser.addParser(":=", "addOperator");
        Parser.addParser("+", "addOperator");
        Parser.addParser("-", "addOperator");
        Parser.addParser("*", "addOperator");
        Parser.addParser("/", "addOperator");
        Parser.addParser("<", "addOperator");
        Parser.addParser(">", "addOperator");
        Parser.addParser("<=", "addOperator");
        Parser.addParser(">=", "addOperator");
        Parser.addParser("==", "addOperator");


        Parser.addParser("([0-9]+)", "addConsts");
        Parser.addParser("'[\\40-\\176]*'", "addConsts");

        Parser.addParser("({|}|\\[|\\]|\\(|\\)|\\|)", "addOther");

        // The name of the source code file and it will write the outcome to 'out.txt' automatically
        Tools tool = new Tools("F2C.p");
    }
}
