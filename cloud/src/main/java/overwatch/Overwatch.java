package overwatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class Overwatch {

    public static int port;
    private static final ArrayList<ConfigurableApplicationContext> servers = new ArrayList<>();

    @Bean
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        Overwatch.port = port;
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
            System.out.println("Overwatch is running on port " + port);
        };
    }

    public ConfigurableApplicationContext run() {
        return SpringApplication.run(Overwatch.class);
    }
}
