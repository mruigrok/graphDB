package query;

import engine.Graph;

import java.util.HashSet;
import java.util.Scanner;

public class Parser {
    private Graph g;
    private HashSet<String> keywords; //needed or unnecessary?

    public Parser(Graph db){ //should we use this or keep this class strictly static methods?
        this.g = db;
    }

    //KEYWORDS
    //MATCH, WHERE, RETURN, WITH, CREATE, UPDATE, SELECT, DELETE, INSERT, FROM
    //Design this similar to Cypher

    public static void parseUserInput(){
        Scanner scanner = new Scanner(System. in);
        String input = scanner. nextLine();
        //parse user input
    }
}
