package engine;

import engine.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {
    //my graph interface
    //for now just a collection of node objects
    // how do we want to organize the nodes?
    // do we start with one ?
    // or do we house all of them in one place ?
    //traversal i know,... breadth first search ... depth fist search .. will need to look into this
    // caching ?
    private static int nodeCount = 10000; //TODO: this might be able to be removed with a better datastructure... aither way might be good to have ?

    //TODO: implement
    public boolean isIn(String key){
        //search nodes and find the node if its there
        // can we grab a random node and start looking ?
        return false;
    }

    // Breadth - First - Search
    //TODO: do we assume connectivity ... ?
    public static void BFS(Node node){ // this function is static as it technically does not belong to Graph class (for now)
        //our visited map  //TODO: this does not fucking scale well lmao

        HashMap<String, Boolean> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(node);
        visited.put(node.getHashId(), true);

        while(!queue.isEmpty()){
            Node p = queue.remove();
            System.out.println(p.getLabel());
            for(Node n : p.getConnectedNodes()){ //for-each loop
                if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if not in there OR in there and is set to false ?
                    queue.add(n);
                    visited.put(node.getHashId(), true);
                }
            }
        }
    }

    // Depth - First - Search
    public static void DFS(Node node){ // this function is static as it technically does not belong to Graph class (for now)
        HashMap<String, Boolean> visited = new HashMap<>();
        DFSUtil(node, visited);
    }

    public static void DFSUtil(Node node, HashMap<String, Boolean> visited){
        visited.put(node.getHashId(), true);
        System.out.println(node.getLabel());

        for(Node n : node.getConnectedNodes()){
            if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if haven't visited it
                DFSUtil(n, visited);
            }
        }

    }
}