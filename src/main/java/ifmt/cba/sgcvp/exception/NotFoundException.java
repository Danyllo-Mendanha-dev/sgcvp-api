package ifmt.cba.sgcvp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
// Indica que o recurso solicitado nao foi encontrado.
public class NotFoundException extends Exception{

    // Cria a excecao com mensagem padrao de recurso nao encontrado.
    public NotFoundException(){
        super("Erro ao buscar um recurso");
    }
    // Cria a excecao com mensagem especifica de recurso nao encontrado.
    public NotFoundException(String mensagem){
        super(mensagem);
    }

}
