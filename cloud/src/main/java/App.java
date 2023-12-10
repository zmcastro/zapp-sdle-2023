import database.DBHandler;
import ConsistentHashing.ConsistentHashing;
import org.springframework.context.ConfigurableApplicationContext;
import overwatch.Overwatch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

        Overwatch o = new Overwatch();
        o.setPort(8080);
        ConfigurableApplicationContext oContext = o.run();

        for (int i = 0; i < nServers; i++) {
            createServer();
        }


    }

    private static void createServer() {
        try {
            String apiUrl = "http://localhost:8080/add";

            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("PUT");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
