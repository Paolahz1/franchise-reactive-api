package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final String applicationName;
    private final String serverUrl;

    public OpenApiConfig(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.url:http://localhost:8080}") String serverUrl) {
        this.applicationName = applicationName;
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .version("1.0.0")
                        .description("Reactive REST API for managing franchises, branches, and products")
                        .contact(new Contact()
                                .name("Bancolombia")
                                .email("support@bancolombia.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Current Environment Server")
                ));
    }
}
