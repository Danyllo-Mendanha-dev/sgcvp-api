package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class ClienteDTO {

    private int codigo;

    @NotBlank(message = "Razao social e obrigatoria")
    @Size(max = 200, message = "Razao social deve possuir no maximo 200 caracteres")
    private String razaoSocial;

    @NotBlank(message = "Nome fantasia e obrigatorio")
    @Size(max = 200, message = "Nome fantasia deve possuir no maximo 200 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve possuir 14 digitos")
    private String CNPJ;

    @Size(max = 20, message = "Inscricao estadual deve possuir no maximo 20 caracteres")
    private String inscricaoEstadual;

    @NotBlank(message = "Endereco e obrigatorio")
    @Size(max = 255, message = "Endereco deve possuir no maximo 255 caracteres")
    private String endereco;

    @NotBlank(message = "UF e obrigatoria")
    @Pattern(regexp = "^[A-Z]{2}$", message = "UF deve possuir duas letras maiusculas")
    private String UF;

    @NotNull(message = "Promotor e obrigatorio")
    private PromotorDTO promotor;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
