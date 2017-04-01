package pers.oj;

/**
 * Created by 97520 on 03/08/2017.
 */
public class Edge {
    private Status start;   // The left side of the edge
    private Status end;     // The right side of the edge
    private String contentSet;  // The acceptable character set
    private String rawStr;      // The original regular expression
    private boolean isUnion;    // If the expression is parsed successfully.
                                // If not, it will be true
    private boolean isComplement;   // If it is the opposite of the contentSet.
                                    // If so, it will be true

    // The constructor for edge with content
    Edge(String src, Status pre, Status post) {
        start = pre;
        end = post;
        contentSet = "";
        rawStr = src;
        constructContent(src);
        pre.addOutEdge(this);
        post.addInEdge(this);
    }

    // The constructor for empty edge
    Edge(Status pre, Status post) {
        start = pre;
        end = post;
        contentSet = null;
        pre.addOutEdge(this);
        post.addInEdge(this);
    }

    // Judge if this is an empty edge
    public boolean isEmpty() {
        return contentSet == null;
    }

    // For debug
    // Return the regular expression
    public String getRawStr() {
        return rawStr;
    }

    // Return the left side of the edge
    public Status getStart() {
        return start;
    }

    // Return the right side of the edge
    public Status getEnd() {
        return end;
    }

    // If the content set match the string
    public boolean match(String item) {
        if (isUnion) {
            if (item.equals(contentSet)) return true;
            return false;
        }

        if (isComplement) {
            if (contentSet.indexOf(item) == -1) return true;
            return false;
        }

        if (isEmpty()) return false;

        if (contentSet.indexOf(item) != -1) return true;
        return false;
    }

    // Add the expression to the content set
    private void constructContent(String src) {
        switch (src.charAt(0)) {
            case '[': pair(src); break;
            case '"': character(src.substring(1));
            default: character(src);
        }
    }

    // Case for '[<expression>]'
    private int offset;
    private void pair(String src) {
        if (src.charAt(src.length() - 1) != ']') {
            failMessage(src);
            return;
        }
        isUnion = false;

        int i = 1 + checkComplementOrDash(src.charAt(1));
        char pre = 0;
        for (; i < src.length() - 1; i++) {
            switch (src.charAt(i)) {
                case '-':
                    addRange(pre, src.substring(i + 1));
                    i += offset;
                    break;
                case '\\':
                    pre = convertASCII(src.substring(i + 1));
                    i += offset;
                    break;
                default:
                    pre = src.charAt(i);
                    contentSet += pre;
            }
        }
    }

    // Case for single character
    private void character(String src) {
        if (src.isEmpty() || src.length() > 1) {
            failMessage(src);
            return;
        }

        contentSet = src;
        isUnion = true;
    }

    // Out put the error message to console
    private void failMessage(String src) {
        System.out.println("Error: '" + src + "'");
        contentSet = new String();
        isUnion = true;
    }

    // Check the first character of <expression> in '[]'
    // If so, add the offset
    // ^ means reverse
    // - means add '-' to content set
    private int checkComplementOrDash(char item) {
        offset = 0;

        if (item == '^') {
            isComplement = true;
            offset++;
        }

        if (item == '-') {
            contentSet += '-';
            offset++;
        }

        return offset;
    }

    // Deal with '[character-character]'
    private void addRange(char pre, String item) {
        offset = 0;
        char post;

        if (item.charAt(0) == '\\') post = convertASCII(item.substring(1));
        else post = item.charAt(0);

        offset++;
        pre++;

        for (; pre <= post; pre++)
            contentSet += pre;
    }

    // Convert ASCII to character
    private char convertASCII(String item) {
        offset = 0;
        String ch = "";

        while (item.charAt(offset) >= '0' && item.charAt(offset) <= '9') {
            ch += item.charAt(offset++);
        }

        offset--;
        return (char)Integer.parseInt(ch);
    }
}
