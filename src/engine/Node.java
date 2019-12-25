package engine;

import java.lang.reflect.Array;
import java.util.*;

public class Node {
    private String hashId = ""; //unique id for our node
    private String label = ""; // a label for the node, hardcoded to a string for now, this can be variable
    private String properties = ""; // a property/data for the node, again hardcoded as string and will change in future

    //TODO:
    // we need a data structure that will take in a "edge", this edge points to "n" number of other points
    // this will also have to contain the TYPE of relation, this relation is determined by the user!
    // each node has a list of other nodes it can go to!
    // for each node it can go to, there is also a "relationship type / edge description" example: a friend, connection, follower
    // therefore, we have a dynamic list of node:relation pairs ? where the node is the key... this seems like a terrible idea ...but will use for now!

    private  HashMap<String, ArrayList<Node> > relationMap; //TODO: get rid of having both... no good keeping track of two variables
    private ArrayList<Node> connectedNodes;

    public Node(){
        //everything is already initialized, just allocate the relationMap
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.hashId = UUID.randomUUID().toString();
        this.connectedNodes = new ArrayList<Node>();
    }

    public Node(String label) {
        this.label = label;
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.hashId = UUID.randomUUID().toString();
        this.connectedNodes = new ArrayList<Node>();
    }
    public Node(String label, String properties){
        this.label = label;
        this.properties = properties;
        this.relationMap = new HashMap<String, ArrayList<Node> >();
        this.hashId = UUID.randomUUID().toString();
        this.connectedNodes = new ArrayList<Node>();
    }

    public String getLabel() {
        return label;
    }
    public String getHashId() {
        return hashId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    //TODO: write functions for checking relations, listing all connected nodes ..


    public ArrayList<Node> getConnectedNodes() {
        return connectedNodes;
    }

    public void addNode(Node newNode, String relation){
        //add this new pair to hashmap
        //find relation
        if(this.relationMap.containsKey(relation)){
            //NEED TO TEST
            this.relationMap.get(relation).add(newNode); //is this sufficient ? or do we have to this (lines below)
            //STUPID//
            //ArrayList<Node> temp = this.relationMap.get(relation);
            //temp.add(newNode);
            //this.relationMap.put(relation, temp );
            //STUPID//
        }else{
            this.relationMap.put(relation, new ArrayList<Node>() {{add(newNode);}}  ); // cant believe that worked lol
        }
        this.connectedNodes.add(newNode);
    }

    public ArrayList<Node> getNodesGivenRelation(String relation){
        return this.relationMap.get(relation);
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
