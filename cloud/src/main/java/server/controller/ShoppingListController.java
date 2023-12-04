package server.controller;

import database.DBHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ConsistentHashing.ConsistentHashing;

@RestController
@RequestMapping("/")
public class ShoppingListController {

    private final ConsistentHashing consistentHashing;
    private final DBHandler dbHandler;

    public ShoppingListController(ConsistentHashing consistentHashing, DBHandler dbHandler) {
        this.consistentHashing = consistentHashing;
        this.dbHandler = dbHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getShoppingList(@PathVariable Long id) {
        String server = consistentHashing.getNode(String.valueOf(id));
        try {
            String data = dbHandler.getFile(server, id);
            return ResponseEntity.ok(data);
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

        String server = consistentHashing.getNode(String.valueOf(id));
        try {
            dbHandler.storeFile(server, id, jsonData);
            return ResponseEntity.ok("Shopping List with ID " + id + " stored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error storing Shopping List with ID: " + id);
        }
    }

}
