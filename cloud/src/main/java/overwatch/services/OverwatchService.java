package overwatch.services;

import ConsistentHashing.ConsistentHashing;
import database.DBHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import server.Server;

import java.util.HashMap;

@Service
public class OverwatchService {
    private HashMap<Integer, ConfigurableApplicationContext> servers = new HashMap<>();
    private Integer lastPort = 8081;

    private ConsistentHashing consistentHashing;

    private DBHandler dbHandler;

    public OverwatchService(){
        consistentHashing = new ConsistentHashing(5);
        dbHandler = new DBHandler(consistentHashing);

        // Initialization code for shared instances
        consistentHashing.addNode("db_1");
        consistentHashing.addNode("db_2");
        consistentHashing.addNode("db_3");
        consistentHashing.addNode("db_4");
        consistentHashing.addNode("db_5");
    }

    public void addServer() {
        Server s = new Server();
        s.setPort(lastPort);
        s.setConsistentHashing(consistentHashing);
        s.setDBHandler(dbHandler);
        ConfigurableApplicationContext context = s.run();
        System.out.println("After run");

        servers.put(lastPort, context);
        lastPort += 1;

    }

    public void removeServer(Integer port) {
        ConfigurableApplicationContext server = servers.remove(port);
        if (server != null) {
            server.stop();

            lastPort = port;
        }
    }

    public HashMap<Integer, ConfigurableApplicationContext> getServers() {
        return new HashMap<Integer, ConfigurableApplicationContext>(servers);
    }

    public Integer getLastPort() {
        return lastPort;
    }
}
