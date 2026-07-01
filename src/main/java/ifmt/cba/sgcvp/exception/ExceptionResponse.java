package ifmt.cba.sgcvp.exception;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta padronizada para erros retornados pela API.")
// Padroniza o corpo de resposta para erros da API.
public record ExceptionResponse( 
    @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-06-30T14:30:00Z")
    Date data, 
    @Schema(description = "Mensagem resumida do erro", example = "Recurso nao encontrado")
    String mensagem, 
    @Schema(description = "Detalhes tecnicos ou caminho da requisicao", example = "/cliente/codigo/99")
    String detalhes) { }
