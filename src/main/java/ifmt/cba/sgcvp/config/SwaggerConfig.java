package ifmt.cba.sgcvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
// Configura metadados exibidos na documentacao Swagger.
public class SwaggerConfig {

    @Bean
    // Cria a definicao OpenAPI usada pelo Swagger UI.
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGCVP API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento dos servicos da aplicacao SGCVP.")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("suporte@sgcvp.com"))
                        .termsOfService("Termos de uso da API"));
    }
}
