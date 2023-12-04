package server;

import database.DBHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ConsistentHashing.ConsistentHashing;

@SpringBootApplication
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
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