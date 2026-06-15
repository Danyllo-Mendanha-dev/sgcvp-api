package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class FornecedorDTO {

    private int codigo;

    @NotBlank(message = "Razao social e obrigatoria")
    @Size(max = 200, message = "Razao social deve possuir no maximo 200 caracteres")
    private String razaoSocial;

    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve possuir 14 digitos")
    private String CNPJ;

    @NotBlank(message = "Endereco e obrigatorio")
    @Size(max = 255, message = "Endereco deve possuir no maximo 255 caracteres")
    private String endereco;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
