package engine;


//TODO:
// we need a data structure that will take in a "edge", this edge points to "n" number of other points
// this will also have to contain the TYPE of relation, this relation is determined by the user!
// each node has a list of other nodes it can go to!
// for each node it can go to, there is also a "relationship type / edge description" example: a friend, connection, follower
// therefore, we have a dynamic list of node:relation pairs ? where the node is the key... this seems like a terrible idea ...but will use for now!


import java.lang.reflect.Array;
import java.util.*;

public class Node {
    private String hashId = ""; //unique id for our node
    private String label = ""; // a label for the node, hardcoded to a string for now, this can be variable
    private String properties = ""; // a property/data for the node, again hardcoded as string and will change in future
    private  HashMap<String, ArrayList<Node> > relationMap; //TODO: get rid of having both... no good keeping track of two variables
    private ArrayList<Node> connectedNodes;

    public Node(){
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.connectedNodes = new ArrayList<Node>();
        this.hashId = UUID.randomUUID().toString();
    }

    public Node(String label) {
        this.label = label;
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.connectedNodes = new ArrayList<Node>();
        this.hashId = UUID.randomUUID().toString();
    }
    public Node(String label, String properties){
        this.label = label;
        this.properties = properties;
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.connectedNodes = new ArrayList<Node>();
        this.hashId = UUID.randomUUID().toString();
    }

    public String getLabel() {
        return label;
    }
    public String getHashId() {
        return hashId;
    }

    public ArrayList<Node> getConnectedNodes() {
        return connectedNodes;
    }
    public ArrayList<Node> getNodesGivenRelation(String relation){
        return this.relationMap.get(relation);
    }
    public String getProperties() {
        return properties;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void addNode(Node newNode, String relation){
        //add this new pair to hashmap
        //find relation
        if(this.relationMap.containsKey(relation)){
            this.relationMap.get(relation).add(newNode); //is this sufficient ? or do we have to this (lines below)
        }else{
            this.relationMap.put(relation, new ArrayList<Node>() {{add(newNode);}}  ); // cant believe that worked lol
        }
        this.connectedNodes.add(newNode);
    }

    public void removeNodesByRelation(String relation){
      if(this.relationMap.containsKey(relation)){
        this.relationMap.remove(relation);
        //remove list of connected node with this relationship
      }
      else{
        System.out.println("Cannot find node/relationship combination");
      }

    }

    //The whole point of this.connectedNodes was so this function is not needed , faster run time, but more memory!
    /*
    public ArrayList<Node> getAllNodes(String relation){
        ArrayList<Node> temp =  new ArrayList<Node>();
        //the below code is N time, technically
        for (Map.Entry<String, ArrayList<Node> > entry : this.relationMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            ArrayList<Node> temp1 = entry.getValue();
            for(int i=0; i<temp1.size(); i++){
                temp.add(temp1.get(i));
            }
        }
        return temp;
    }
     */
}
