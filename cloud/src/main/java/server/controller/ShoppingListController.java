package server.controller;

import database.DBHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ConsistentHashing.ConsistentHashing;
import server.model.ShoppingList;

import java.util.Random;

@RestController
@RequestMapping("/")
public class ShoppingListController {

    private final ConsistentHashing consistentHashing;
    private final DBHandler dbHandler;
    private final Integer testValue;

    public ShoppingListController(ConsistentHashing consistentHashing, DBHandler dbHandler) {
        this.consistentHashing = consistentHashing;
        this.dbHandler = dbHandler;
        this.testValue = new Random().nextInt(0, 100);
        System.out.println("This is my random value: " + testValue);
    }

    @GetMapping("/")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("This is my random value: " + testValue);
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getShoppingList(@PathVariable String id) {
        String node = consistentHashing.getNode(String.valueOf(id));
        try {
            String data = dbHandler.getFile(node, id);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reading Shopping List with ID: " + id);
        }
    }

    @PutMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateShoppingList(@PathVariable String id, @RequestBody String jsonData) {

        String node = consistentHashing.getNode(id);

        String storedShoppingList;

        try {
            storedShoppingList = dbHandler.getFile(node, id);
        } catch (Exception e) {
            try {
                dbHandler.storeFile(node, id, jsonData);
                return ResponseEntity.ok("Shopping List with ID " + id + " stored successfully");
            } catch (Exception e1) {
                return ResponseEntity.status(500).body("Error storing Shopping List with ID: " + id);
            }
        }

        ShoppingList oldShoppingList = new ShoppingList(id, "old"); // id and name will be replaced
        ShoppingList newShoppingList = new ShoppingList(id, "new"); // id and name will be replaced

        oldShoppingList.fromJSON(storedShoppingList);
        newShoppingList.fromJSON(jsonData);

        oldShoppingList.join(newShoppingList);

        String mergedList = oldShoppingList.toJSON();

        try {
            dbHandler.storeFile(node, id, mergedList);
            return ResponseEntity.ok("Shopping List with ID " + id + " stored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error storing Shopping List with ID: " + id);
        }
    }

}
