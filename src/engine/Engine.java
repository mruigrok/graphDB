package engine;

import java.util.HashMap;
import engine.Graph;

public class Engine {
    private static HashMap<String, Graph> graphs = new HashMap<String, Graph>();
    public static void initialize(){
        //TODO: restore data from storage class, will return list of existing graphs!
        System.out.println("DB is up!");

    }
    public static void createGraph(String name){
        graphs.put(name, new Graph(name));
    }
    public static void dropGraph(String name){
        graphs.remove(name);
    }
    public static Graph getGraph(String name){
        return graphs.get(name);
    }
}
