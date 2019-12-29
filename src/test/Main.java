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

    public static Graph createDummyGraph(){
        //create new graphs, with vertices and relationships
        Graph g = new Graph("test1");

        g.addVertice("Reza");
        g.addVertice("Ruify");
        g.addVertice("Callum");
        g.addVertice("Wassyng");
        g.addVertice("Stinky");
        g.addVertice("Raccoon");
        g.addVertice("Brad");
        g.addVertice("Malcolm");
        g.addVertice("Eric");

        g.addRelation("Reza", "Callum", "friend");
        g.addRelation("Reza", "Wassyng", "friend");
        g.addRelation("Reza", "Ruify", "friend");
        g.addRelation("Reza", "Brad", "friend");

        g.addRelation("Ruify", "Stinky", "friend");
        g.addRelation("Ruify", "Raccoon", "friend");

        g.addRelation("Brad", "Malcolm", "friend");
        g.addRelation("Brad", "Eric", "friend");

        return g;
    }

    public static void test_BFS(){
        //set io to byte array
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        createDummyGraph().BFS("Reza"); // bfs search from reza node

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


        createDummyGraph().DFS("Reza"); // bfs search from reza node

        //reset io stream
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String expectedVal = "Reza\nCallum\nWassyng\nRuify\nStinky\nRaccoon\nBrad\nMalcolm\nEric\n";

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
