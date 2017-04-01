package pers.oj;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by 97520 on 03/08/2017.
 */
public class Status {
    private ArrayList<Edge> inEdges;
    private ArrayList<Edge> outEdges;
    private boolean finalStatus;    // If this is a terminal status
    static int count = 0;   // The number of total status
    public int id;          // The id of temporary status
    Method methods;         // If this is a terminal status. it would have methods

    // The constructor of non-terminal status
    Status() {
        inEdges = new ArrayList<>();
        outEdges = new ArrayList<>();
        finalStatus = false;
        id = count++;
    }

    // The constructor of terminal status with action function name
    Status(String method) {
        inEdges = new ArrayList<>();
        outEdges = new ArrayList<>();
        finalStatus = true;
        id = count++;

        // Try to reflect the method
        try {
            methods = Tools.class.getMethod(method, int.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Wrong Method at " + method);
        }
    }

    // Status of no us
    Status(boolean init) {
        id = -1;
    }

    // If the status is terminal
    // If so, return true
    public boolean isFinal() {
        return finalStatus;
    }

    public void addInEdge(Edge item) {
        inEdges.add(item);
    }

    public void addOutEdge(Edge item) {
        outEdges.add(item);
    }

    public ArrayList<Edge> getInEdges() {
        return inEdges;
    }

    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }
}

