package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHandler {

    private static final String JSON_DIRECTORY = "src/main/java/database/";
    public String getFile(String node, Long id) throws IOException {
        List<String> nodes = getNodes(node);
        String filePath;

        Exception lastException = null;

        for (String n : nodes) {
            try {
                filePath = JSON_DIRECTORY + n + "/" + id + ".json";
                String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
                return jsonData;
            } catch (Exception e) {
                lastException = e;
                // continue to the next attempt
            }
        }

        if (lastException != null) {
            throw new IOException("Failed to read data from all nodes", lastException);
        } else {
            throw new IOException("No exception was caught, but all attempts failed");
        }
    }

    public void storeFile(String node, Long id, String jsonData) throws IOException {
        List<String> nodes = getNodes(node);
        String filePath;
        try {
            for (String n: nodes){
                filePath = JSON_DIRECTORY + n + "/" + id + ".json";
                Files.write(Paths.get(filePath), jsonData.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> getNodes(String node){
        List<String> nodes = new ArrayList<>(Arrays.asList("db_1", "db_2", "db_3", "db_4", "db_5")); // update this when adding/removing nodes
        if (!nodes.contains(node)){
            return new ArrayList<>();
        }
        if (node.equals(nodes.get(nodes.size()-2))){
            return new ArrayList<>(Arrays.asList(node, nodes.get(nodes.size()-1), nodes.get(0)));
        }
        if (node.equals(nodes.get(nodes.size()-1))){
            return new ArrayList<>(Arrays.asList(node, nodes.get(0), nodes.get(1)));
        }
        return new ArrayList<>(Arrays.asList(node, nodes.get(nodes.indexOf(node) + 1), nodes.get(nodes.indexOf(node) + 2)));
    }
}
