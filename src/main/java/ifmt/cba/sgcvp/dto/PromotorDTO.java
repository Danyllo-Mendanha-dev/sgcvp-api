package ifmt.cba.sgcvp.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class PromotorDTO {

    private int codigo;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150, message = "Nome deve possuir no maximo 150 caracteres")
    private String nome;

    @Valid
    @NotEmpty(message = "Promotor deve possuir ao menos um municipio de atuacao")
    private List<MunicipioDTO> listaMunicipio = new ArrayList<MunicipioDTO>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
