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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Storage {
    //Need to work on this -> Identifying a users database from other users
    //ensure the name is a valid filename (no illegal characters etc.), maybe in graph class we can do this
    private String fileName;                //where the file will be saved. Potentially by the user and graph name
    private String storageType = ".json";    //How the data is stored is not relevant to user, just developer. Can change as seen fit
    private String userDirectory = "";      //where it will be stored on the users device

    public Storage(){
        //constructor
    }

    public void setFileName(String name){
        this.fileName = name;
    }

    public static void storeGraphAs(Graph graphToStore){
        //have proper error handling
        Storage graphStorage = new Storage();
        graphStorage.setFileName(graphToStore.getName());
        graphStorage.saveGraph(graphToStore);
    }

    private void saveGraph(Graph g){
        switch (this.storageType) {
            case ".json":
                writeStringtoFile(convertGraphtoJsonString(g), g);
                break;
            case ".txt":
                writeStringtoFile(convertGraphtoJsonString(g), g);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.storageType);
        }
    }

    private String convertGraphtoJsonString(Graph g){
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

        return graphData.toJSONString();
    }

    private Graph getGraphFromJsonFormat(String JSONfilename) throws IOException, ParseException {
        //TODO: Find correct directory for the json file
        String directory = System.getProperty("user.home");
        directory += "\\graphDBstorage";
        Path path = Paths.get(directory, JSONfilename);
        String content = new String(Files.readAllBytes(path));

        //converting String to JSON object
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(content);
        JSONArray nodes = (JSONArray) jsonObj.get("nodes");
        JSONArray relations = (JSONArray) jsonObj.get("relations");

        //might have to get another name for the graph name
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

    private void writeStringtoFile(String content, Graph g){
        //save as a JSON file, this is temporary
        //TODO:Finding user directories  depending on the project they are working on
        String directory = System.getProperty("user.home");
        directory += "\\graphDBstorage";
        String filename =  g.getName() + this.storageType;

        //write to file
        Path path = Paths.get(directory, filename);
        try {
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            // exception handling
        }
    }

    public Graph getGraph() throws IOException, ParseException {
        //go to specific users file on record and look for their stored db's
        //TODO: Correct filenames
        return this.getGraphFromJsonFormat("test1.json");
    }

    //function to create a directory in the users home
    public void createDirectory(){
        System.out.println("Enter the name of the new directory: ");
        Scanner scan = new Scanner(System.in);
        Path dir = Paths.get(System.getProperty("user.home") + "\\" + scan.next());

        if(!Files.exists(dir)){
            try{
                //create the directory
                Files.createDirectory(dir);
            }catch(IOException e){
                //print the error
                e.printStackTrace();
            }
        }
    }




}
