package server.controller;

import database.DBHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ConsistentHashing.ConsistentHashing;

import java.util.List;

@RestController
@RequestMapping("/db")
public class NodeController {

    private final ConsistentHashing consistentHashing;
    private final DBHandler dbHandler;

    public NodeController(ConsistentHashing consistentHashing, DBHandler dbHandler) {
        this.consistentHashing = consistentHashing;
        this.dbHandler = dbHandler;
    }

    @GetMapping("/list")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> listDBNodes() {
        try {
            String data = dbHandler.listNodes().toString();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unable to list database nodes");
        }
    }

    @PutMapping("/{node}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addNode(@PathVariable String node){
        try {
            List<String> currentNodes = dbHandler.listNodes();
            if (currentNodes.contains(node)){
                return ResponseEntity.status(500).body("Unable to create new node with name: " + node + "; Node already exists");
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to check database");
        }

        // create a new node

        try{
            dbHandler.createNode(node);
            return ResponseEntity.status(200).body("Created new node with name: " + node);
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Unable to create new node with name: " + node);
        }
    }

    @DeleteMapping("/{node}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> removeNode(@PathVariable String node){
        try {
            List<String> currentNodes = dbHandler.listNodes();
            if (currentNodes.size() <= 3){
                return ResponseEntity.status(500).body("Unable to remove node with name: " + node + "; Cannot have less than 3 database nodes");
            }
            if (!currentNodes.contains(node)){
                return ResponseEntity.status(500).body("Unable to remove node with name: " + node + "; Node does not exist");
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to check database");
        }

        // remove node

        try{
            dbHandler.removeNode(node);
            return ResponseEntity.status(200).body("Removed node with name: " + node);
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Unable to remove node with name: " + node);
        }
    }

}

