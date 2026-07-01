package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Dados de um fornecedor.")
public class FornecedorDTO {

    @Schema(description = "Codigo identificador do fornecedor", example = "1")
    private int codigo;

    @Schema(description = "Razao social do fornecedor", example = "Distribuidora Alfa Ltda")
    @NotBlank(message = "Razao social e obrigatoria")
    @Size(max = 200, message = "Razao social deve possuir no maximo 200 caracteres")
    private String razaoSocial;

    @Schema(description = "CNPJ do fornecedor com 14 digitos, sem pontuacao", example = "98765432000110")
    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve possuir 14 digitos")
    private String CNPJ;

    @Schema(description = "Endereco comercial do fornecedor", example = "Avenida Brasil, 500")
    @NotBlank(message = "Endereco e obrigatorio")
    @Size(max = 255, message = "Endereco deve possuir no maximo 255 caracteres")
    private String endereco;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
