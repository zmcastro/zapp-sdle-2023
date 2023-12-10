import database.DBHandler;
import ConsistentHashing.ConsistentHashing;
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

        ConsistentHashing consistentHashing = new ConsistentHashing(5);
        DBHandler dbHandler = new DBHandler(consistentHashing);

        // Initialization code for shared instances
        consistentHashing.addNode("db_1");
        consistentHashing.addNode("db_2");
        consistentHashing.addNode("db_3");
        consistentHashing.addNode("db_4");
        consistentHashing.addNode("db_5");

        for (int i = 0; i < nServers; i++) {
            Server s = new Server();
            s.setPort(8080 + i);
            s.setConsistentHashing(consistentHashing);
            s.setDBHandler(dbHandler);
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
