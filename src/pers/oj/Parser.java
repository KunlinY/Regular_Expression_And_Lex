package pers.oj;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by 97520 on 03/08/2017.
 * Modified by 97520 on 03/20/2017.
 */
public class Parser {
    private static Status begin = new Status(); // The begin of all NFA

    private ArrayList<Status> allStatus;    // The list for a single parser to build NFA
    private Status lexBegin;    // The begin of a specific parser
    private Status lexEnd;      // The terminal of a specific parser

    private String regex;       // Regular expression
    private int offset;         // The index of regular expression

    // Constructor to build a NFA
    // str is the regular expression
    // method is the name of the action method
    Parser(String str, String method) {
        allStatus = new ArrayList<>();

        lexBegin = new Status();
        lexEnd = new Status(method);

        allStatus.add(lexBegin);
        allStatus.add(lexEnd);

        regex = str;
        offset = 0;

        parse(lexBegin, lexEnd);
    }

    // Add new Parser by creating a Parser object
    public static void addParser(String str, String method) {
        Parser item = new Parser(str, method);
        addEmptyEdge(begin, item.lexBegin);
    }

    // The core function to see if it is matched
    // Return -1 if failed
    // Return the index of matched prefix if succeed
    public static int match(String text, int index) {
        HashSet<Status> states = new HashSet<>();
        states.addAll(getEmptyStates(begin));

        // Ignore the blank space
        if (text.charAt(index) == ' '
                || text.charAt(index) == '\t'
                || text.charAt(index) == '\n') {
            return -1;
        }

        return subMatch(text, index, states);
    }

    // To be use recursively in order to match the longest prefix
    // Return -1 if failed
    // Return the index of matched prefix if succeed
    public static int subMatch(String text, int index, HashSet<Status> states) {
        // If the index out of bound, fail
        if (index >= text.length())
            return -1;

        // Initialize the state
        HashSet<Status> tempState = getStates(states, text.charAt(index));

        // No state is added, fail
        if (tempState.isEmpty())
            return -1;

        // Ignore blank spaces
        if (text.charAt(index) == ' '
                || text.charAt(index) == '\t'
                || text.charAt(index) == '\n')
            return -1;

        // Call it self recursively to deal with the next character
        int key = subMatch(text, index + 1, tempState);

        // If not failed
        if (key != -1)
            return key;

        // Find all the terminal status
        ArrayList<Status> finalState = new ArrayList<>();
        for (Status state : tempState)
            if (state.isFinal())
                finalState.add(state);

        // If no terminal is found, fail
        if (finalState.isEmpty())
            return -1;

        // Find the one with the highest priority
        Status item = finalState.get(0);
        for (Status state : finalState)
            if (item.id > state.id)
                item = state;

        // Try to reflect the method
        try {
            Tools tool = new Tools();
            item.methods.invoke(tool, index);
        } catch (Exception e) {
            System.out.println("Error invoking the method with status " + item.hashCode());
            e.printStackTrace();
        } finally {
            return index;
        }
    }

    private void print(HashSet<Status> states) {
        for (Status state : states)
            System.out.print(state.id + " ");
        System.out.println();
    }

    private static HashSet<Status> getEmptyStates(Status state) {
        HashSet<Status> newStates = new HashSet<>();
        newStates.add(state);

        int oldSize = 0;
        while (oldSize != newStates.size()) {
            oldSize = newStates.size();
            HashSet<Status> temp = new HashSet<>();

            for (Status item : newStates)
                for (Edge edge : item.getOutEdges())
                    if (edge.isEmpty())
                        temp.add(edge.getEnd());

            newStates.addAll(temp);
        }

        return newStates;
    }

    private static HashSet<Status> getStates(HashSet<Status> oldStates, char alpha) {
        HashSet<Status> newStates = new HashSet<>();

        for (Status state : oldStates) {
            for (Edge edge : state.getOutEdges()) {
                if (edge.match(String.valueOf(alpha)))
                    newStates.addAll(getEmptyStates(edge.getEnd()));
            }
        }

        return newStates;
    }

    /*******************Construct NFA***********************/
    // Core function to construct NFA
    private void parse(Status begin, Status end) {
        Status temp = begin;
        Status subEnd = new Status(true);

        while (offset < regex.length()) {
            char tempChar = regex.charAt(offset);
            switch (tempChar) {
                case '(':
                    subEnd = new Status();
                    allStatus.add(subEnd);
                    offset++;
                    parse(temp, subEnd);
                    repeat(temp, subEnd);
                    temp = subEnd;
                    break;
                case '|':
                    addEmptyEdge(temp, end);
                    subEnd = new Status();
                    allStatus.add(subEnd);
                    offset++;
                    parse(begin, subEnd);
                    repeat(temp, subEnd);
                    temp = subEnd;
                    break;
                case ')':
                    offset++;
                    addEmptyEdge(temp, end);
                    return;
                default:
                    subEnd = new Status();
                    addEdge(terminal(), temp, subEnd);
                    allStatus.add(subEnd);
                    repeat(temp, subEnd);
                    temp = subEnd;
            }
        }
        addEmptyEdge(subEnd, end);
    }

    // Judge * + ?
    // Not repeat return false
    private boolean repeat(Status begin, Status end) {
        try {
            switch (regex.charAt(offset)) {
                case '*':
                    asterisk(begin, end);
                    break;
                case '+':
                    plus(begin, end);
                    break;
                case '?':
                    questionMark(begin, end);
                    break;
                default: return false;
            }
            offset++;
        } catch (StringIndexOutOfBoundsException e) {
            // Judge the last character
            offset--;
            if (!repeat(begin, end)) offset++;
        }
        return true;
    }

    // repeat any times including zero
    private void asterisk(Status begin, Status end) {
        addEmptyEdge(begin, end);
        addEmptyEdge(end, begin);
    }

    // repeat at least once
    private void plus(Status begin, Status end) {
        addEmptyEdge(end, begin);
    }

    // ? repeat once or not
    private void questionMark(Status begin, Status end) {
        addEmptyEdge(begin, end);
    }

    private static void addEmptyEdge(Status begin, Status end) {
        new Edge(begin, end);
    }

    private void addEdge(String str, Status begin, Status end) {
        new Edge(str, begin, end);
    }

    // Deal with terminal, the condition of an edge
    private  String terminal() {
        String key = new String();

        switch (regex.charAt(offset)) {
            case '[': // set of characters
                while (regex.charAt(offset) != ']')
                    key += regex.charAt(offset++);
                key += ']';
                break;
            case '"': // string consts
                key += regex.charAt(offset++);
                while (regex.charAt(offset) != '"')
                    key += regex.charAt(offset++);
                break;
            case '\\': // escape character
                key += regex.charAt(++offset);
                break;
            default: // single character
                key += regex.charAt(offset);
                break;
        }

        offset++;
        return key;
    }
}
