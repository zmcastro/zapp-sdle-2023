package overwatch.services;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import server.Server;

import java.util.HashMap;

@Service
public class OverwatchService {
    private HashMap<Integer, ConfigurableApplicationContext> servers = new HashMap<>();
    private int lastPort = 8081;

    public void addServer() {
        Server s = new Server();
        s.setPort(lastPort);
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

    public int getLastPort() {
        return lastPort;
    }

    public void setLastPort(Integer port) {
        this.lastPort = port;
    }

    public ConfigurableApplicationContext getServer(Integer port) {
        return servers.get(port);
    }
}
