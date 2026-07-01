package ifmt.cba.sgcvp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
// Indica que os dados enviados na requisicao sao invalidos.
public class NotValidDataException extends Exception{

    // Cria a excecao com mensagem padrao de validacao.
    public NotValidDataException(){
        super("Erro de validacao dos dados");
    }
    // Cria a excecao com mensagem especifica de validacao.
    public NotValidDataException(String mensagem){
        super(mensagem);
    }
}
