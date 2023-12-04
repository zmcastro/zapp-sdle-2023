import org.springframework.context.ConfigurableApplicationContext;
import server.Server;

import java.util.ArrayList;

public class App {
    private static final ArrayList<ConfigurableApplicationContext> servers = new ArrayList<>();

    public static void main(String[] args) {
        int nServers = 3;
        try {
            nServers = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of servers. Using default server count.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No arguments provided. Using default server count.");
        }

        for (int i = 0; i < nServers; i++) {
            Server s = new Server();
            s.setPort(8080 + i);
            ConfigurableApplicationContext context = s.run();
            System.out.println("After run");
            servers.add(context);
        }

        for (ConfigurableApplicationContext server : servers) {
            System.out.println("server.isActive() = " + server.isActive());
            System.out.println("port = " + server.getBean("getPort"));
        }
    }
}
