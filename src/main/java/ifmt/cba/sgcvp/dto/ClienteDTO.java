package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Dados de um cliente atendido pela empresa.")
// Transporta dados cadastrais de cliente pela API.
public class ClienteDTO {

    @Schema(description = "Codigo identificador do cliente", example = "1")
    private int codigo;

    @Schema(description = "Razao social do cliente", example = "Mercado Central Ltda")
    @NotBlank(message = "Razao social e obrigatoria")
    @Size(max = 200, message = "Razao social deve possuir no maximo 200 caracteres")
    private String razaoSocial;

    @Schema(description = "Nome fantasia do cliente", example = "Mercado Central")
    @NotBlank(message = "Nome fantasia e obrigatorio")
    @Size(max = 200, message = "Nome fantasia deve possuir no maximo 200 caracteres")
    private String nomeFantasia;

    @Schema(description = "CNPJ do cliente com 14 digitos, sem pontuacao", example = "12345678000199")
    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve possuir 14 digitos")
    private String CNPJ;

    @Schema(description = "Inscricao estadual do cliente", example = "0012345678")
    @Size(max = 20, message = "Inscricao estadual deve possuir no maximo 20 caracteres")
    private String inscricaoEstadual;

    @Schema(description = "Endereco comercial do cliente", example = "Rua das Flores, 100")
    @NotBlank(message = "Endereco e obrigatorio")
    @Size(max = 255, message = "Endereco deve possuir no maximo 255 caracteres")
    private String endereco;

    @Schema(description = "Unidade federativa do cliente", example = "MT")
    @NotBlank(message = "UF e obrigatoria")
    @Pattern(regexp = "^[A-Z]{2}$", message = "UF deve possuir duas letras maiusculas")
    private String UF;

    @Schema(description = "Promotor responsavel pelo atendimento do cliente")
    @NotNull(message = "Promotor e obrigatorio")
    private PromotorDTO promotor;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
