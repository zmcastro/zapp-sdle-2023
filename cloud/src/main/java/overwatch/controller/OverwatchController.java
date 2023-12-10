package overwatch.controller;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import overwatch.services.OverwatchService;

import java.util.ArrayList;
import java.util.Map;

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

    @GetMapping("/")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getServers() {
        ArrayList<String> servers = new ArrayList<>();
        for (Map.Entry<Integer, ConfigurableApplicationContext> server : overwatchService.getServers().entrySet()) {
            servers.add(String.valueOf(server.getKey()));
        }

        return ResponseEntity.ok(new StringBuilder().append("These are the currently active servers: ").append(servers.toString()).append("\nThis is the next usable port: ").append(overwatchService.getLastPort()).toString());
    }

}
