package test;

import engine.Graph;
import engine.Node;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    //variables like this are ok to global everywhere, not just for this test class! keep this in mind
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String [] args){
        System.out.println("Testing the DB!\n\n");
        System.out.println("Testing BFS:");
        test_BFS();
        System.out.println("Testing DFS:");
        test_DFS();
    }

    public static void test_BFS(){
        //set io to byte array
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        //add friends to some node
        Node t = new Node("Reza");
        t.addNode(new Node("Callum"), "friend");
        t.addNode(new Node("Wassyng"), "friend");
        //create new node with some friends
        Node t1 = new Node("Ruify");
        t1.addNode(new Node("Stinky"), "friend");
        t1.addNode(new Node("Raccoon"), "friend");
        //create new node with some friends
        Node t2 = new Node("Brad");
        t2.addNode(new Node("Malcolm"), "friend");
        t2.addNode(new Node("Eric"), "friend");
        //add the independent nodes to create leveled graph
        t.addNode(t1, "friend");
        t.addNode(t2, "friend");

        Graph.BFS(t);

        //reset io stream
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String expectedVal = "Reza\nCallum\nWassyng\nRuify\nBrad\nStinky\nRaccoon\nMalcolm\nEric\n";

        //check results
        if( out.toString().equals(expectedVal) ){
            successMessage();
        }else{
            failureMessage(expectedVal, out.toString());
        }
    }

    public static void test_DFS(){
        //set io to byte array
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        //add friends to some node
        Node t = new Node("Reza");
        t.addNode(new Node("Callum"), "friend");
        t.addNode(new Node("Wassyng"), "friend");
        //create new node with some friends
        Node t1 = new Node("Ruify");
        t1.addNode(new Node("Stinky"), "friend");
        t1.addNode(new Node("Raccoon"), "friend");
        //add the two independent nodes to create leveled graph
        t.addNode(t1, "friend");

        Graph.DFS(t);

        //reset io stream
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String expectedVal = "Reza\nCallum\nWassyng\nRuify\nStinky\nRaccoon\n";

        //check results
        if( out.toString().equals(expectedVal) ){
            successMessage();
        }else{
            failureMessage(expectedVal, out.toString());
        }
    }

    public static void successMessage(){
        System.out.println(ANSI_GREEN + "success!" + ANSI_RESET);
    }

    public static void failureMessage(String exp, String act){
        System.out.println(ANSI_RED + "expected output:\n" + exp + "\nactual output:\n" + act + ANSI_RESET);
    }
}
