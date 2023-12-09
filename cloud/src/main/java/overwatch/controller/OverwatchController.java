package overwatch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import overwatch.services.OverwatchService;

@RestController
@RequestMapping("/")
public class OverwatchController {

    private final OverwatchService overwatchService = new OverwatchService();

    @PutMapping("/add")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addServer() {
        overwatchService.addServer();

        return ResponseEntity.ok(String.valueOf(overwatchService.getServers()));
    }

    @DeleteMapping("/remove/{port}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> removeServer(@PathVariable String port) {
        overwatchService.removeServer(Integer.valueOf(port));

        return ResponseEntity.ok(String.valueOf(overwatchService.getServers()));
    }



}
