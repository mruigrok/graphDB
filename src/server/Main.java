package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import engine.Engine;
import query.Query;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String [] args) throws IOException {
        //here we need to boot a server, both HTTP and socket connections!!! similar to postgres / DB2 consoles for example!
        System.out.println("Booting server!");

        Engine.initialize();

        //initialize HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(12544), 0);
        server.createContext("/test", new TestDB());
        server.createContext("/query", new QueryDB()); // for now this echos back the values given by the user
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class TestDB implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the test response! -  Ruify";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class QueryDB implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // parse request
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);

            if( parameters.containsKey("query") ){ //just a quick sanity check
                if(Query.executeQuery(parameters)){
                    // send response
                    String response = "";
                    for (String key : parameters.keySet()) {
                        response += key + " => " + parameters.get(key) + "\n";
                    }
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                }
                else{
                    // send response
                    System.out.println("error - missing fields");
                    String response = "missing fields";
                    t.sendResponseHeaders(500, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                }
            }else{
                //throw an error
                System.out.println("error - no query");
                String response = "no query";
                t.sendResponseHeaders(500, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
            }
        }

        public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

            if(query != null){
                String pairs[] = query.split("[&]");
                for (String pair : pairs) {
                    String param[] = pair.split("[=]");
                    String key = null;
                    String value = null;
                    if(param.length > 0){
                        key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                    }

                    if(param.length > 1){
                        value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                    }

                    if(parameters.containsKey(key)){
                        Object obj = parameters.get(key);
                        if(obj instanceof List<?>) {
                            List<String> values = (List<String>) obj;
                            values.add(value);
                        }
                        else if(obj instanceof String){
                            List<String> values = new ArrayList<String>();
                            values.add((String) obj);
                            values.add(value);
                            parameters.put(key, values);
                        }
                    }
                    else{
                        parameters.put(key, value);
                    }
                }
            }
        }
    }
}
