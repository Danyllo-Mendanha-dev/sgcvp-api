package ifmt.cba.sgcvp.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados de uma categoria de produtos.")
public class CategoriaProdutoDTO {

    @Schema(description = "Codigo identificador da categoria de produto", example = "1")
    private int codigo;
    @Schema(description = "Nome da categoria de produto", example = "Bebidas")
    private String nome;
    @Schema(description = "Descricao da categoria de produto", example = "Produtos liquidos destinados a venda")
    private String descricao;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
