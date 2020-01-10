package engine;

import engine.Node;
import engine.Graph;

import java.nio.file.Path;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Storage {
    //Need to work on this -> Identifying a users database from other users
    //ensure the name is a valid filename (no illegal characters etc.), maybe in graph class we can do this
    private String fileName;                //where the file will be saved. Potentially by the user and graph name
    private String storageType = ".json";    //How the data is stored is not relevant to user, just developer. Can change as seen fit
    
    public Storage(){
        //constructor
    }

    //Storage.storeGraphAs(Graph g);

    public static void storeGraphAs(Graph graphToStore){
        //have proper error handling
        Storage graphStorage = new Storage();
        graphStorage.setFileName(graphToStore.getName());
        graphStorage.saveGraph(graphToStore);

    }

    public void setFileName(String name){
        this.fileName = name;
    }

    private void saveGraph(Graph g){
        switch (this.storageType) {
            case ".json":
                saveGraphAsJson(g);
                break;
            case ".txt":
                saveGraphAsTxt(g);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.storageType);
        }
    }

    private void saveGraphAsTxt(Graph g){
        //Save the graph as a txt file
        //Need to create or look for a folder of the user's db's
        String directory = "C:\\graphDB Storage\\"; //need to correct for different devices
        //this is where the username of the user would be used
        //path += "username\\" + this.fileName + ".txt";
        String dataToWrite = "||";
        for (Node n: g.getAllNodesAsArray()){
            dataToWrite += "|" + n.getHashId() + "|" + n.getLabel() + "|";
        }
        dataToWrite += "|";
        for(String s : g.getAllRelationshipKeysAsArray()){
            dataToWrite+= "|" + s + "|";
        }
        //String directory = System.getProperty("user.home");
        String fileName = g.getName()+ ".txt";

        Path path = Paths.get(directory, fileName);

        try {
            Files.write(path, dataToWrite.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // exception handling
        }

    }

    private Graph getGraphFromTxt(String directory) {
        //grab data from .txt file and build the graph for a specific user
        Path path = Paths.get(directory, "test1.txt");
        List<String> list = null;
        try {
            list = Files.readAllLines(path);
            list.forEach(line -> System.out.println(line));
            list.toString();
            System.out.println(list);
        } catch (IOException e) {
            // exception handling
        }
        String graphData = null;
        for (String st : list) {
            graphData += st;
        }

        //for(Character c : graphData){ }

        Graph g = new Graph("G2");

        return g;
    }


    public Graph getGraph() throws IOException, ParseException {
        //go to specific users file on record and look for their stored db's
       return this.getGraphFromJson("test1.json");
    }

    private void saveGraphAsJson(Graph g){
        //save as a JSON file
        String directory = "C:\\graphDB Storage\\";
        String filename =  g.getName() + this.storageType;

        //adding the nodes
        JSONObject graphData = new JSONObject();
        JSONArray nodes = new JSONArray();
        for(Node n : g.getAllNodesAsArray()){
            JSONObject node = new JSONObject();
            node.put("label", n.getLabel());
            node.put("hashID", n.getHashId());
            node.put("properties", n.getProperties());
            nodes.add(node);
        }
        graphData.put("nodes", nodes);

        //adding all of the relations
        JSONArray relations = new JSONArray();
        for(HashMap<String, String> hm : g.getAllRelations()){
            JSONObject relation = new JSONObject();
            relation.put("node1", hm.get("~node1~"));
            relation.put("relation", hm.get("~relation~"));
            relation.put("node2", hm.get("~node2~"));
            //relation.put("relation keys", hm.get("~id~"));
            relations.add(relation);
        }
        graphData.put("relations", relations);

        //write to file
        Path path = Paths.get(directory, filename);
        try {
            Files.write(path, graphData.toJSONString().getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // exception handling
        }

    }

    private Graph getGraphFromJson(String JSONfilename) throws IOException, ParseException {
        //TODO: Find correct directory for the json file
        String directory = "C:\\graphDB Storage\\";
        Path path = Paths.get(directory, JSONfilename);
        String content = new String(Files.readAllBytes(path));
        System.out.println(content);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        JSONArray nodes = (JSONArray) json.get("nodes");
        JSONArray relations = (JSONArray) json.get("relations");

        Graph g = new Graph(JSONfilename);

        //adding the nodes
        Iterator iterator = nodes.iterator();
        while(iterator.hasNext()){
            JSONObject j = (JSONObject)iterator.next();
            g.addVertex(j.get("label").toString(), j.get("properties").toString(), j.get("hashID").toString());
        }

        //adding relations
        iterator = relations.iterator();
        while(iterator.hasNext()){
            JSONObject j = (JSONObject)iterator.next();
            g.addRelation(j.get("node1").toString(), j.get("node2").toString(), j.get("relation").toString());
        }
        return g;
    }


}
