package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/")
public class ShoppingListController {

    private static final String JSON_DIRECTORY = "src/main/java/database/";

    @GetMapping("/{id}")
    public ResponseEntity<String> getShoppingList(@PathVariable Long id) {
        String filePath = JSON_DIRECTORY + "1/" + id + ".json"; // hardcoded for node 1, need to figure out node
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            return ResponseEntity.ok(jsonData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reading Shopping List with ID: " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateShoppingList(@PathVariable Long id, @RequestBody String jsonData) {

        // json from DB
        // json to Shopping List
        // jsonData to Shopping List
        // merge
        // Shopping list to json

        String filePath = JSON_DIRECTORY + "1/" + id + ".json";
        try {
            Files.write(Paths.get(filePath), jsonData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return ResponseEntity.ok("Shopping List with ID " + id + " stored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error storing Shopping List with ID: " + id);
        }
    }

}
