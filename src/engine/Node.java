package engine;

import java.util.UUID;

public class Node {
    private String hashId = ""; //unique id for our node
    private String label = ""; // a label for the node, hardcoded to a string for now, this can be variable
    private String properties = ""; // a property/data for the node, again hardcoded as string and will change in future

    //constructor
    public Node(){
        this.hashId = UUID.randomUUID().toString();
    }

    //static method belonging to class not individual object
    public static Node createWithLabel(String label){
        Node newNode = new Node();
        newNode.setLabel(label);
        return newNode;
    }

    //static method belonging to class not individual object
    public static Node createWithLabelandProperties(String label, String properties){
        Node newNode = new Node();
        newNode.setLabel(label);
        newNode.setProperties(properties);
        return newNode;
    }

    //keep these? Or use the previous createNode functions? Or both?
    public Node(String label) {
        this.label = label;
        this.hashId = UUID.randomUUID().toString();
    }

    public Node(String label, String properties){
        this.label = label;
        this.properties = properties;
        this.hashId = UUID.randomUUID().toString();
    }

    public String getLabel() {
        return label;
    }

    public String getHashId() {
        return hashId;
    }

    public String getProperties() {
        return properties;
    }

    public void setLabel(String label){ this.label = label;}

    public void setProperties(String properties){ this.properties = properties;}

    public void updateLabel(String label) {
        this.label = label;
    }

    public void updateProperties(String properties) {
        this.properties = properties;
    }

}
