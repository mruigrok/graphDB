package engine;

import engine.Node;

import java.lang.reflect.Array;
import java.util.*;

public class Graph {

    private HashMap<String, Node> allNodes;
    private ArrayList<HashMap> relations;
    private HashSet<String> relationKeys;
    private String name = "";

    public Graph(String name){
        this.name = name;
        this.allNodes = new HashMap<String, Node>();
        this.relationKeys = new HashSet<String>();
        this.relations = new ArrayList<HashMap>();
    }

    public void printAllNodes(){
        for(String i : this.allNodes.keySet()){
            System.out.println(i);
        }
    }

    public void printAllRelationships(){ System.out.println(this.relationKeys); }

    public int numberOfVertices(){ return allNodes.size(); }

    public void addVertice(String label){
        allNodes.put(label, new Node(label));
    }

    public void addVertice(String label, String property){
        allNodes.put(label, new Node(label, property));
    }

    public void removeVertex(Node vertex){
        String label = vertex.getLabel();
        if(allNodes.containsKey(label)){
            //remove relation and relationKey between nodes
            for(Node r : getOutgoingConnectedNodes(vertex)){
                for(String s : findRelations(vertex, r)){
                    //in case of multiple relations between the two
                    removeRelation(vertex, r, s);
                }
            }
            for(Node r: getIncomingConnectedNodes(vertex)){
                for(String s : findRelations(r, vertex)){
                    removeRelation(r, vertex, s);
                }
            }
            this.allNodes.remove(label);
        }
        else{
            System.out.println("err: can't find vertex");
        }
    }

    public void removeRelation(Node node1, Node node2, String relation){
        /* error check- if you cant find nodes don't bother with relationships */
        if(!isIn(node1.getLabel())) {
            //System.err.println("err: couldnt find: " + node1.getLabel());
            return;
        }
        if(!isIn(node2.getLabel())) {
            //System.err.println("err: couldnt find: " + node2.getLabel());
            return;
        }
        String rKey = node1.getLabel() + relation + node2.getLabel();
        for(HashMap<String, String> r : this.relations){
            if(r.get("~id~").equals(rKey)){
                /* remove if found */
                this.relations.remove(r);
                this.relationKeys.remove(rKey);
                System.out.println("success: removed relationship");
                return;
            }
        }
        System.out.println("err: couldn't find node-relationship pair");
    }

    //Maybe change the string type to Node? just to save the .getLabel() later on
    public void addRelation(String node1, String node2, String relation){
        //error check
        if(!this.allNodes.containsKey(node1) ) {
            System.err.println("err: couldnt find: " + node1);
            return;
        }
        if(!this.allNodes.containsKey(node2)){
            System.err.println("err: couldnt find: " + node2);
            return;
        }
        //create relationKey
        String relationKey = node1+relation+node2;
        //add to set of relationKeys
        this.relationKeys.add(relationKey);
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("~relation~", relation);
        properties.put("~node1~", node1);
        properties.put("~node2~", node2);
        properties.put("~id~", relationKey);
        //add to set of relations
        this.relations.add(properties);
    }

    public ArrayList<String> findRelations(Node node1, Node node2){
        //find all the relationships in the direction of node 1 to node 2
        ArrayList<String> relations = new ArrayList<String>();
        for(HashMap<String, String> r : this.relations){
            if(r.get("~node1~").equals(node1.getLabel()) && r.get("~node2~").equals(node2.getLabel())){
                relations.add(r.get("~relation~"));
            }
        }
        return relations;
    }

    public void updateRelation(Node node1, Node node2, String oldRelation, String newRelation){
        //need some conditional to ensure their is the old relationship ****WORK ON THIS******
        removeRelation(node1, node2, oldRelation);
        addRelation(node1.getLabel(), node2.getLabel(), newRelation);
    }

    public Node findNode(String label){
        if(allNodes.containsKey(label)){
            return allNodes.get(label);
        }
        else{
            return null;
        }
    }

    public boolean isIn(String label){
        return allNodes.containsKey(label);
    }

    public ArrayList<Node> getOutgoingConnectedNodes(Node node, String relation){
        String label = node.getLabel();
        ArrayList<Node> connections = new ArrayList<Node>();

        for(HashMap<String, String> r : this.relations){
            if(relation == null && r.get("~node1~") == label){
                connections.add(this.allNodes.get(r.get("~node2~")));
            }
            else if(relation == r.get("~relation~") && r.get("~node1~") == label){
                connections.add(this.allNodes.get(r.get("~node2~")));
            }
        }

        return connections;
    }

    public ArrayList<Node> getIncomingConnectedNodes(Node node, String relation){
        String label = node.getLabel();
        ArrayList<Node> connections = new ArrayList<Node>();

        for(HashMap<String, String> r : this.relations){
            if(relation == null && r.get("~node2~") == label){
                connections.add(this.allNodes.get(r.get("~node1~")));
            }
            else if(relation == r.get("~relation~") && r.get("~node2~") == label){
                connections.add(this.allNodes.get(r.get("~node1~")));
            }
        }

        return connections;
    }

    public ArrayList<Node> getOutgoingConnectedNodes(Node node){
        return getOutgoingConnectedNodes(node, null);
    }

    public ArrayList<Node> getIncomingConnectedNodes(Node node){
        return getIncomingConnectedNodes(node, null);
    }

    // Breadth - First - Search
    public void BFS(Node node){ // this function is static as it technically does not belong to Graph class (for now)
        //our visited map  //TODO: this does not fucking scale well lmao

        HashMap<String, Boolean> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(node);
        visited.put(node.getHashId(), true);

        while(!queue.isEmpty()){
            Node p = queue.remove();
            System.out.println(p.getLabel());
            for(Node n : this.getOutgoingConnectedNodes(p)){ //for-each loop
                if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if not in there OR in there and is set to false ?
                    queue.add(n);
                    visited.put(n.getHashId(), true);
                }
            }
        }
    }

    public void BFS(String node){
        this.BFS(this.findNode(node));
    }

    // Depth - First - Search
    public void DFS(Node node){ // this function is static as it technically does not belong to Graph class (for now)
        HashMap<String, Boolean> visited = new HashMap<>();
        DFSUtil(node, visited);
    }

    //private?
    public void DFSUtil(Node node, HashMap<String, Boolean> visited){
        visited.put(node.getHashId(), true);
        System.out.println(node.getLabel());

        for(Node n : this.getOutgoingConnectedNodes(node)){
            if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if haven't visited it
                DFSUtil(n, visited);
            }
        }

    }

    public void DFS(String node){
        this.DFS(this.findNode(node));
    }



}
