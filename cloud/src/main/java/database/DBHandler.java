package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DBHandler {

    private static final String JSON_DIRECTORY = "src/main/java/database/";
    public String getFile(String server, Long id) throws IOException {
        String filePath = JSON_DIRECTORY + server + "/" + id + ".json";
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            return jsonData;
        } catch (Exception e) {
            throw e;
        }
    }

    public void storeFile(String server, Long id, String jsonData) throws IOException {
        String filePath = JSON_DIRECTORY + server + "/" + id + ".json";
        try {
            Files.write(Paths.get(filePath), jsonData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw e;
        }
    }
}
