package test;

import engine.*;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
//import String.utils;

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
        System.out.println("Testing removing vertex:");
        test_removeVertex();
        //can also use test_removeRelation
        System.out.println("Testing isPath:");
        test_isPath();
    }

    public static Graph createDummyGraph(){
        //create new graphs, with vertices and relationships
        Graph g = new Graph("test1");

        g.addVertex("Reza");
        g.addVertex("Ruify");
        g.addVertex("Callum");
        g.addVertex("Wassyng");
        g.addVertex("Stinky");
        g.addVertex("Raccoon");
        g.addVertex("Brad");
        g.addVertex("Malcolm");
        g.addVertex("Eric");

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
        String expectedVal = "RezaCallumWassyngRuifyBradStinkyRaccoonMalcolmEric";

        //check results
        if( out.toString().replaceAll("\\s+","").equals(expectedVal) ){
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
        String expectedVal = "RezaCallumWassyngRuifyStinkyRaccoonBradMalcolmEric";

        //check results
        if( out.toString().replaceAll("\\s+","").equals(expectedVal) ){
            successMessage();
        }else{
            failureMessage(expectedVal, out.toString());
        }
    }

    public static void test_removeVertex(){
        Graph g = createDummyGraph();

        g.removeVertex("Raccoon");
        g.removeVertex("Eric");
        g.removeVertex(g.getNode("Malcolm"));

        g.removeRelation(g.getNode("Reza"), g.getNode("Ruify"), "friend");
        g.updateRelation("Reza", "Callum", "friend", "buddies");
        g.updateRelation(g.getNode("Reza"), g.getNode("Callum"), "buddies", "enemy");

        if( g.isIn("Raccoon") || g.isIn("Eric") || g.isIn("Malcolm")){ // these should all be false
            failureMessage("", "Raccoon or Eric or Malcolm in graph!");
            return;
        }

        if(g.isRelation("RezafriendRuify")){
            failureMessage("relation RezafriendRuify should be gone!", "relation RezafriendRuify still exists");
            return;
        }

        if(!g.isRelation("RezaenemyCallum")){
            failureMessage("relation RezaenemyCallum", "relation RezafriendRuify doesn't exist");
            return;
        }

        successMessage();
    }

    public static void test_isPath(){
        Graph g = createDummyGraph();
        if( !g.isPath( g.getNode("Reza"), g.getNode("Callum") ) ){
            failureMessage("true", "false");
            return;
        }

        if( g.isPath( g.getNode("Callum"), g.getNode("Ruify") ) ){
            failureMessage("false", "true");
            return;
        }

        if( !g.isPath( g.getNode("Reza"), g.getNode("Malcolm") ) ){
            failureMessage("true", "false");
            return;
        }

        if( !g.isPath( g.getNode("Reza"), g.getNode("Callum"), "friend" ) ){
            failureMessage("true", "false");
            return;
        }

        if( g.isPath( g.getNode("Callum"), g.getNode("Ruify"), "friend" ) ){
            failureMessage("false", "true");
            return;
        }

        if( g.isPath( g.getNode("Reza"), g.getNode("Malcolm"), "friends" ) ){
            failureMessage("false", "true");
            return;
        }

        successMessage();

    }

    public static void successMessage(){
        System.out.println(ANSI_GREEN + "success!" + ANSI_RESET);
    }

    public static void failureMessage(String exp, String act){
        System.out.println(ANSI_RED + "expected output:\n" + exp + "\nactual output:\n" + act + ANSI_RESET);
    }
}
