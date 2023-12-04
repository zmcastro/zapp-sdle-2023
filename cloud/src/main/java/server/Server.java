package server;

import database.DBHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ConsistentHashing.ConsistentHashing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Server {

    public static int port;

    @Bean
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        Server.port = port;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            factory.setPort(port);
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("Server is running on port " + port);
        };
    }

    public ConfigurableApplicationContext run() {
        return SpringApplication.run(Server.class);
    }

    @Bean
    public ConsistentHashing consistentHashing() {
        return new ConsistentHashing(5);
    }

    @Bean
    public DBHandler dbHandler() {
        return new DBHandler();
    }

    @Bean
    public ApplicationRunner init(ConsistentHashing consistentHashing, DBHandler dbHandler) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // Your initialization code here
                consistentHashing.addNode("db_1");
                consistentHashing.addNode("db_2");
                consistentHashing.addNode("db_3");
                consistentHashing.addNode("db_4");
                consistentHashing.addNode("db_5");
            }
        };
    }

    @Bean
    public ConsistentHashing consistentHashing() {
        return new ConsistentHashing(5);
    }

    @Bean
    public DBHandler dbHandler() {
        return new DBHandler();
    }

    @Bean
    public ApplicationRunner init(ConsistentHashing consistentHashing, DBHandler dbHandler) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // Your initialization code here
                consistentHashing.addNode("db_1");
                consistentHashing.addNode("db_2");
                consistentHashing.addNode("db_3");
                consistentHashing.addNode("db_4");
                consistentHashing.addNode("db_5");
            }
        };
    }
}