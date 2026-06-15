package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class MunicipioDTO {

    private int codigo;

    @Size(max = 150, message = "Nome deve possuir no maximo 150 caracteres")
    private String nome;

    @Pattern(regexp = "^[A-Z]{2}$", message = "UF deve possuir duas letras maiusculas")
    private String UF;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
