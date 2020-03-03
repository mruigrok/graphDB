package engine;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.parsers.SAXParser;

/*Store information attached to projects such as:
* Creation date, users, last saved, size, etc. in a json file with the project
* This can be used later for use in other applications such as MVCC
* */

/*
* File Hierarchy:
* GraphDB working dir -> Storage -> Project -> associated graphs
*
* Restoring Projects:
*
* Restoring a project from the disk involves getting all the graph json strings from the file
* that represent the graph and mapping them to their graph name in a hash map. Each graph is
* stored in a json string in the form {graph name}.json. A collection of graphs is called a
* project. The variable SavedProjects stores that 'project' hash map into another hash map that
* maps the project to the given project Name.
*
* Building Projects:
*
* Building a project involves creating a Graph instance for each one of the saved graphs in that project.
* With the Graph object you can perform operations such as insertion, deletion etc. and then later store
* the changes back to the disk by re-writing the file.
*
* */


/*Working on saving projects to the disk and mutual exclusion*/

public class Storage {
    //TODO: Manage multiple users, multiple graphs, readers/writers etc.
    //FIXME : Create or restore one graph only at a time if wanted, mutual exclusion!

    private HashMap<String, HashMap> SavedProjects;
    private HashMap<String, HashMap> BuiltProjects;

    public Storage(){
        if(!isStorageDirInWorking()){
            createStorageDirInWorking();
        }
        else{
            BuiltProjects = new HashMap<String, HashMap>();
            restoreAllSavedProjects();
        }
    }

    //Create a directory in the GraphDB home directory
    public void createStorageDirInWorking(){
        Path dir = Paths.get(System.getProperty("user.dir") + "\\storage");
        try{
            Files.createDirectory(dir);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Make sure the Storage folder is in the working directory
    public boolean isStorageDirInWorking(){
        Path dir = Paths.get(System.getProperty("user.dir") + "\\Storage");
        return  Files.exists(dir);
    }

    public HashMap<String, HashMap> getSavedProjects(){
        return this.SavedProjects;
    }

    public void printBuiltGraphs(){
        for(String s: BuiltProjects.keySet()){
            HashMap<String, Graph> hm = BuiltProjects.get(s);
            for(String i : hm.keySet()){
                hm.get(i).printAllNodes();
            }
        }
    }

    //Default Storage location for the projects
    public String getStorageDir(){
        return System.getProperty("user.dir") + "\\Storage";
    }

    public void saveAll(){
        /*Go through the steps to save all work so user doesn't have to call all of
        * the worker functions*/
    }

    //Display to output all of the projects
    public void displayAllProjects(){
        if(!SavedProjects.isEmpty()){
            System.out.println(SavedProjects.keySet());
        }
        else{
            System.out.println("no projects to display!");
        }
    }

    //List all of the graphs in a certain project
    public void displayAllGraphsInProject(String projectName){
        if(SavedProjects.containsKey(projectName)){
            System.out.println(SavedProjects.get(projectName).keySet());
        }
        else{
            System.out.println("error: cannot find project");
        }
    }

    //Create a new project with the name projectName
    public void createNewProject(String projectName){
        //Ensure it is given a proper name that doesn't already exist
        if(!projectName.equals("")) {
            Path dir = Paths.get(getStorageDir() + "/" + projectName);
            if(Files.exists(dir)){
                System.out.println("Directory already exists!");
            }
            else {
                try {
                    Files.createDirectory(dir);
                    if (Files.exists(dir)) {
                        //Directory was successfully created, allocate HashMap fro project
                        HashMap<String, String> project = new HashMap<>();
                        SavedProjects.put(projectName, project);
                    } else {
                        System.out.println("error: did not create directory");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            System.out.println("error: directory needs a name");
        }
    }

    //Used to restore/recover all of the users project data, graphs and information from the file
    public void restoreAllSavedProjects(){
        HashMap <String, HashMap> graphs = new HashMap<String, HashMap>();
        Path dir = Paths.get(getStorageDir());
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(dir, entry -> Files.isDirectory(entry));
            for (Path entry : paths){
                //All of the project folders in the Storage directory
                HashMap<String, String> projectGraphs = new HashMap<String, String>();
                String projectName = entry.getFileName().toString();
                DirectoryStream<Path> stream = Files.newDirectoryStream(entry, "*.json");
                for (Path p : stream) {
                    /*Find all of the .json files in the Storage directory,
                      Extract graph name and add the Graph json string*/
                    String[] split = p.getFileName().toString().split(".json");
                    String graphName = split[0];
                    projectGraphs.put(graphName, new String(Files.readAllBytes(p)));
                }
                graphs.put(projectName, projectGraphs);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        this.SavedProjects = graphs;
    }//end of restoreAllSavedProjects

    //Restore a single project given by projectName
    public void restoreProject(String projectName){
        Path dir = Paths.get(getStorageDir());
        try {
            /*Find the project name in the saved projects and the build all the
            * strings associated with it into graphs and add them to the BuiltProjects*/
            DirectoryStream<Path> paths = Files.newDirectoryStream(dir, entry -> Files.isDirectory(entry));
            for (Path entry : paths) {
                String projName = entry.getFileName().toString();
                if (projName.equals(projectName)) {
                    HashMap<String, String> project = new HashMap<String, String>();
                    DirectoryStream<Path> stream = Files.newDirectoryStream(entry, "*.json");
                    for (Path p : stream) {
                        String[] split = p.getFileName().toString().split(".json");
                        project.put(split[0], new String(Files.readAllBytes(p)));
                    }
                    SavedProjects.put(projName, project);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }//end of restoreProject

    //Build the Graphs of all projects
    public void buildAllProjects(){
        if(!SavedProjects.isEmpty()){
            for(String s: SavedProjects.keySet()){
                HashMap<String, String> hm = SavedProjects.get(s);
                HashMap<String, Graph> graphs = new HashMap<>();
                for(String i : hm.keySet()){
                    graphs.put(i,buildGraphFromJson(hm.get(i)));
                }
                BuiltProjects.put(s, graphs);
            }
        }
        else{
            System.out.println("error: no projects to build");
        }
    }

    //Build a single project with name projectName
    public void buildProject(String projectName){
        if(!SavedProjects.isEmpty()) {
            if (SavedProjects.containsKey(projectName)) {
                HashMap<String, String> project = SavedProjects.get(projectName);
                HashMap<String, Graph> graphs = new HashMap<>();
                for (String i : project.keySet()) {
                    graphs.put(i, buildGraphFromJson(project.get(i)));
                }
                BuiltProjects.put(projectName, graphs);
            } else {
                System.out.println("error: No project found");
            }
        }
        else{
            System.out.println("error: no projects to build");
        }
    }

    public ArrayList<Graph> getAllGraphsFromProject(String projectName){
        //Build a new list of all the graphs in that project
        ArrayList<Graph> graphs = new ArrayList<>();
        if(BuiltProjects.containsKey(projectName)){
            HashMap<String, Graph> graphMap = BuiltProjects.get(projectName);
            for(String s: graphMap.keySet()){
                graphs.add(graphMap.get(s));
            }
        }
        else{
            System.out.println("error: project does not exist or is not built");
        }
        return graphs;
    }

    //Get a specific graph given by graphName from the project denoted by project
    public Graph getGraphFromProject(String project, String graphName){
        if(BuiltProjects.containsKey(project)){
            if(BuiltProjects.get(project).containsKey(graphName)){
                return (Graph) BuiltProjects.get(project).get(graphName);
            }
            else{
                System.out.println("error: graph doesn't exist within the project");
            }
        }
        else{
            //The project is not built so build it and then get the graph
            if(SavedProjects.containsKey(project)){
                buildProject(project);
                if(BuiltProjects.get(project).containsKey(graphName)){
                    return (Graph) BuiltProjects.get(project).get(graphName);
                }
                else{
                    System.out.println("error: graph doesn't exist within the project");
                }
            }
            else {
                System.out.println("error: cannot find the project");
            }
        }
        //If the graph cannot be found, return a null graph
        return new Graph("null");
    }

    //Save the graph g in the project given by projectName
    public void addGraphToProject(Graph g, String projectName){
        //If it has a name and the project is in the Saved Projects
        if(!g.getName().equals("")){
            if(SavedProjects.containsKey(projectName)){
                HashMap<String, String> project = SavedProjects.get(projectName);
                if(project.containsKey(g.getName())){
                    System.out.println("error: This graph already exists!");
                    /*Get user input on whether they want to rename
                    * and if yes what that new name is. Then try to
                    * re-add the graph to the project */
                }
                else{
                    //Good to add project
                    project.put(g.getName(), convertGraphToJsonString(g));
                }
            }
            else{
                System.out.println("error: project doesn't exist yet");
            }
        }
        else{
            System.out.println("error: graph needs a name before saving!");
        }
    }

    //Write all of the projects and there graphs to the disk
    public void writeAllProjectsToDisk(){
        String directory = getStorageDir();
        for(String projectDir : SavedProjects.keySet()){
            HashMap<String, String> graphs = SavedProjects.get(projectDir);
            for(String s: graphs.keySet()){
                Path dir = Paths.get(directory + "/" + projectDir + "/" + s + ".json");
                writeToFile(dir, graphs.get(s));
            }
        }
    }

    //Write a single project to the disk given by the project name projectName
    public void writeProjectToDisk(String projectName){
        if(SavedProjects.containsKey(projectName)){
            String directory = getStorageDir();
            HashMap<String, String> graphs = SavedProjects.get(projectName);
            for(String s : graphs.keySet()){
                Path dir = Paths.get(getStorageDir() + "/" + projectName);
                writeToFile(dir, graphs.get(s));
            }
        }
        else{
            System.out.println("error: No project of that name exists");
        }
    }

    //Write the Graph string to a file given its path
    private void writeToFile(Path path, String content){
        //Delete the existing file if it exists
        if(Files.exists(path)){
            try{
                Files.delete(path);
            }catch(IOException e){
                System.out.println("error: Cannot delete existing file");
                return;
            }
        }
        //Write the new string to the file
        try {
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("error: Cannot write to file");
        }
    }

    //Convert Graph object to a Json string and return as String
    private String convertGraphToJsonString(Graph g){
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

    //Build a Graph from the given json String
    private Graph buildGraphFromJson(String name){
        Graph g = new Graph(name);
        try{
            //converting String to JSON object
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(name);
            JSONArray nodes = (JSONArray) jsonObj.get("nodes");
            JSONArray relations = (JSONArray) jsonObj.get("relations");

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

        }catch (ParseException e){
            System.out.println("error: Cannot build graph");
        }
        return g;
    }

    //Check to see if the directory exists
    public boolean isDirectory(String path){
        Path dir = Paths.get(path);
        return Files.exists(dir);
    }

    /*To delete...probably*/

    //I think this is unneeded now
    /*Get the Graph Sting from a file*/
    private String getStringFromFile(String filename){
        try {
            String dir = getStorageDir();
            Path path = Paths.get(dir, filename + ".json");
            return new String(Files.readAllBytes(path));

        }catch(IOException e){
            System.out.println("error: Cannot find path!");
            return "";
        }
    }

}//end of Storage class
