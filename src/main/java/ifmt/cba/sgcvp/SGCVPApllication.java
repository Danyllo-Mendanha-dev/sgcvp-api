package ifmt.cba.sgcvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Classe principal responsavel por iniciar a aplicacao Spring Boot.
public class SGCVPApllication {

	// Dispara o carregamento do contexto Spring e dos servicos da API.
	public static void main(String[] args) {
		SpringApplication.run(SGCVPApllication.class, args);
		
	}

}
