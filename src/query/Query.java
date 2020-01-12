package query;

import engine.*;
import java.util.HashMap;

public class Query {
    public static boolean executeQuery(HashMap<String, Object> parameters){
        if(checkQuery(parameters)){
            //now we know we are safe and query things
            Graph g;
            switch(parameters.get("query").toString().toLowerCase()) {
                case "insert":
                    //grab the graph
                    g = Engine.getGraph(parameters.get("graph").toString());
                    g.addVertex(
                            parameters.get("label").toString(),
                            parameters.containsKey("properties") == true ? parameters.get("properties").toString() : ""
                    );
                    break;
                case "add relation":
                    //grab the graph
                    g = Engine.getGraph(parameters.get("graph").toString());
                    g.addRelation(
                            parameters.get("from").toString(),
                            parameters.get("to").toString(),
                            parameters.get("relation").toString()
                    );
                    break;
                case "remove relation":
                    g = Engine.getGraph(parameters.get("graph").toString());
                    g.removeRelation(
                            parameters.get("from").toString(),
                            parameters.get("to").toString(),
                            parameters.get("relation").toString()
                    );
                    break;
                case "delete":
                    g = Engine.getGraph(parameters.get("graph").toString());
                    g.removeVertex(
                        parameters.get("label").toString()
                    );
                    break;
                case "update node":
                    g = Engine.getGraph(parameters.get("graph").toString());
                    Node temp = g.getNode(parameters.get("label").toString());
                    if(parameters.containsKey("newLabel")){
                        temp.setLabel(parameters.get("newLabel").toString());
                    }
                    if(parameters.containsKey("newProperties")){
                        temp.setProperties(parameters.get("newProperties").toString());
                    }
                    break;
                case "update relation":
                    g = Engine.getGraph(parameters.get("graph").toString());
                    g.updateRelation(
                            parameters.get("from").toString(),
                            parameters.get("to").toString(),
                            parameters.get("oldRelation").toString(),
                            parameters.get("newRelation").toString()
                    );
                    break;
                case "create graph":
                    Engine.createGraph(
                            parameters.get("graph").toString()
                    );
                    break;
                case "drop graph":
                    Engine.dropGraph(
                            parameters.get("graph").toString()
                    );
                    break;
                case "find":
                    //TODO: lots of work here
                    break;
                default:
                    // code block
                    break;
            }
        }
        else{
            //throw error
            System.out.println("Should throw an exception HERE!!!! - TODO");
            return false;
        }
        return true;
    }
    private static boolean checkQuery(HashMap<String, Object> parameters){
        //TODO: do all error checking here for the queries received from client
        if(!parameters.containsKey("graph")){
            return false;
        }
        if(parameters.get("query").toString().toLowerCase() != "create graph" && parameters.get("query").toString().toLowerCase() != "drop graph"){ //in this condition check to see if graph is there
           Graph g = Engine.getGraph(parameters.get("graph").toString());
           if(g == null) {
               return false;
           }
        }
        switch(parameters.get("query").toString().toLowerCase()) {
            case "insert":
            case "delete":
                //for inserts we need to check if there is a name
                if(!parameters.containsKey("label") ){
                    return false;
                }
                break;
            case "add relation":
            case "remove relation":
                if(!parameters.containsKey("from") || !parameters.containsKey("to") || !parameters.containsKey("relation")){
                    return false;
                }
                break;
            case "update node":
                if(!parameters.containsKey("label")){
                    return false;
                }
                if(!parameters.containsKey("newLabel") && !parameters.containsKey("newProperties")){
                    return false;
                }
                break;
            case "update relation":
                if(!parameters.containsKey("from") || !parameters.containsKey("to") || !parameters.containsKey("oldRelation") || !parameters.containsKey("newRelation")){
                    return false;
                }
                break;
            case "create graph":
            case "drop graph":
                //redundant check, will leave this for now
                if(!parameters.containsKey("graph")){
                    return false;
                }
                break;
            case "find":
                //TODO: lots of work here
                //we need some traversal language ??, create our own
                break;
            default:
                // code block
        }
        return true;
    }
}
