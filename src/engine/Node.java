package engine;

import java.util.UUID;

public class Node {
    private String hashId = ""; //unique id for our node
    private String label = ""; // a label for the node, hardcoded to a string for now, this can be variable
    private String properties = ""; // a property/data for the node, again hardcoded as string and will change in future

    public Node(){
        this.hashId = UUID.randomUUID().toString();
    }

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

    public void updateLabel(String label) {
        this.label = label;
    }

    public void updateProperties(String properties) {
        this.properties = properties;
    }

}
