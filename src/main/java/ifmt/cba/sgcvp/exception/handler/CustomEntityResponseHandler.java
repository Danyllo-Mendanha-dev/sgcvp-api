package ifmt.cba.sgcvp.exception.handler;

import ifmt.cba.sgcvp.exception.ExceptionResponse;
import ifmt.cba.sgcvp.exception.EstoqueInsuficienteException;
import ifmt.cba.sgcvp.exception.NotFoundException;
import ifmt.cba.sgcvp.exception.NotValidDataException;
import ifmt.cba.sgcvp.exception.TransicaoEstadoInvalidaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
// Converte excecoes da aplicacao em respostas HTTP padronizadas.
public class CustomEntityResponseHandler  extends ResponseEntityExceptionHandler{

    @ExceptionHandler(Exception.class)
    // Trata erros nao previstos como erro interno.
    public final ResponseEntity<ExceptionResponse> handleAllException(Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(
            new Date(), 
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    // Trata recursos inexistentes como resposta 404.
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(
            new Date(), 
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotValidDataException.class)
    // Trata dados invalidos como resposta 400.
    public final ResponseEntity<ExceptionResponse> handleValidDataException(Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(
            new Date(), 
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    // Trata falta de estoque como resposta 422.
    public final ResponseEntity<ExceptionResponse> handleEstoqueInsuficienteException(Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TransicaoEstadoInvalidaException.class)
    // Trata transicoes invalidas de status como resposta 422.
    public final ResponseEntity<ExceptionResponse> handleTransicaoEstadoInvalidaException(Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    // Trata falhas de validacao Bean Validation como resposta 400.
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
            .findFirst()
            .orElse("Dados invalidos");

        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            mensagem,
            request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }




}
