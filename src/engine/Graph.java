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

    public String getName(){
        return this.name;
    }

    public void clearGraph(){
        this.relationKeys.clear();
        this.relations.clear();
        this.allNodes.clear();
    }

    public void printAllNodes(){
        for(String i : this.allNodes.keySet()){
            System.out.println(i);
        }
    }

    public void printAllRelationships(){ System.out.println(this.relationKeys); }

    public int numberOfVertices(){ return allNodes.size(); }

    public Node getNode(String label){
        if(allNodes.containsKey(label)){
            return allNodes.get(label);
        }
        else{
            return null;
        }
    }

    public ArrayList<Node> getAllNodesAsArray(){
        ArrayList<Node> allNodes = new ArrayList<Node>();
        for(Node i: this.allNodes.values()){
            allNodes.add(i);
        }
        return allNodes;
    }

    public ArrayList<String> getAllRelationshipKeysAsArray(){
        ArrayList<String> allRelationKeys = new ArrayList<String>();
        for(String i : this.relationKeys){
          allRelationKeys.add(i);
        }
        return allRelationKeys;
    }

    public ArrayList<HashMap> getAllRelations(){
        return this. relations;
    }

    public boolean isIn(String label){
        return allNodes.containsKey(label);
    }

    public void addVertex(String label){
        allNodes.put(label, new Node(label));
    }

    public void addVertex(String label, String property){
        allNodes.put(label, new Node(label, property));
    }

    public void addVertex(String label, String property, String hashID){
        allNodes.put(label, new Node(label, property, hashID));
    }

    public void addVertexWithHashID(String hashID, String label){
        Node newNode = new Node(label);
        newNode.setHashId(hashID);
        this.allNodes.put(label, newNode);
    }

    public void removeVertex(Node vertex){
        if(vertex == null){
            System.out.println("err: can't find vertex");
        }else{this.removeVertex(vertex.getLabel());}
    }

    public void removeVertex(String vertex){
        if(allNodes.containsKey(vertex)){
            //remove relation and relationKey between nodes
            for(Node r : getOutgoingConnectedNodes(getNode(vertex))){
                for(String s : findRelations(vertex, r.getLabel())){
                    //in case of multiple relations between the two
                    removeRelation(vertex, r.getLabel(), s);
                }
            }
            for(Node r: getIncomingConnectedNodes(getNode(vertex))){
                for(String s : findRelations(r.getLabel(), vertex)){
                    removeRelation(r.getLabel(), vertex, s);
                }
            }
            this.allNodes.remove(vertex);
        }
        else{
            System.out.println("err: can't find vertex");
        }
    }

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

    public void removeRelation(Node node1,Node node2, String relation){
        this.removeRelation(node1.getLabel(), node2.getLabel(), relation);
    }

    public void removeRelation(String node1, String node2, String relation){
        /* error check- if you cant find nodes don't bother with relationships */
        if(!isIn(node1)) {
            //System.err.println("err: couldnt find: " + node1);
            return;
        }
        if(!isIn(node2)) {
            //System.err.println("err: couldnt find: " + node2);
            return;
        }
        String rKey = node1 + relation + node2;
        for(HashMap<String, String> r : this.relations){
            if(r.get("~id~").equals(rKey)){
                /* remove if found */
                this.relations.remove(r);
                this.relationKeys.remove(rKey);
                //System.out.println("success: removed relationship");
                return;
            }
        }
        System.out.println("err: couldn't find node-relationship pair");
    }

    public boolean isRelation(String key){
        return relationKeys.contains(key);
    }

    public ArrayList<String> findRelations(String node1, String node2){
        //find all the relationships in the direction of node 1 to node 2
        ArrayList<String> relations = new ArrayList<String>();
        for(HashMap<String, String> r : this.relations){
            if(r.get("~node1~").equals(node1) && r.get("~node2~").equals(node2)){
                relations.add(r.get("~relation~"));
            }
        }
        return relations;
    }

    public ArrayList<String> findOutgoingRelations(String node1){
        ArrayList<String> outgoingRelations = new ArrayList<String>();
        for(HashMap<String, String> r : this.relations){
            if(r.get("~node1~").equals(node1)){
                outgoingRelations.add((r.get("~relation~")));
            }
        }
        return outgoingRelations;
    }

    public ArrayList<String> findIncomingRelations(String node1) {
        ArrayList<String> incomingRelations = new ArrayList<String>();
        for(HashMap<String, String> r : this.relations){
            if(r.get("~node2~").equals(node1)){
                incomingRelations.add((r.get("~relation~")));
            }
        }
        return incomingRelations;
    }

    public void updateRelation(Node node1, Node node2, String oldRelation, String newRelation){
        this.updateRelation(node1.getLabel(), node2.getLabel(), oldRelation, newRelation);
    }

    public void updateRelation(String node1, String node2, String oldRelation, String newRelation){
        //TODO: optimize this
        removeRelation(node1, node2, oldRelation);
        addRelation(node1, node2, newRelation);
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
        this.BFS(this.getNode(node));
    }

    // Depth - First - Search
    public void DFS(Node node){ // this function is static as it technically does not belong to Graph class (for now)
        HashMap<String, Boolean> visited = new HashMap<>();
        DFSUtil(node, visited);
    }
    
    private void DFSUtil(Node node, HashMap<String, Boolean> visited){
        visited.put(node.getHashId(), true);
        System.out.println(node.getLabel());

        for(Node n : this.getOutgoingConnectedNodes(node)){
            if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if haven't visited it
                DFSUtil(n, visited);
            }
        }

    }

    public void DFS(String node){
        this.DFS(this.getNode(node));
    }

    public boolean isPath(Node node1, Node node2, String relation){ // for isPath we copied the BFS algorithm
        if(node1 == null || node2 == null){
            return false;
        }

        String lookingFor = node2.getHashId();
        HashMap<String, Boolean> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(node1);
        visited.put(node1.getHashId(), true);

        while(!queue.isEmpty()){
            Node p = queue.remove();
            if(p.getHashId() == lookingFor){
                return true;
            }
            for(Node n : this.getOutgoingConnectedNodes(p, relation)){ //for-each loop
                if(!visited.containsKey(n.getHashId()) || !visited.get(n.getHashId())){ //if not in there OR in there and is set to false ?
                    queue.add(n);
                    visited.put(n.getHashId(), true);
                }
            }
        }
        return false;
    }

    public boolean isPath(Node node1, Node node2) {
        return this.isPath(node1, node2, null);
    }


}//end of Graph class
