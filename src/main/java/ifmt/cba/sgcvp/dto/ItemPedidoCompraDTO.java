package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Item que compoe um pedido de compra.")
public class ItemPedidoCompraDTO {

    @Schema(description = "Codigo identificador do item do pedido de compra", example = "1")
    private int codigo;

    @Schema(description = "Quantidade comprada do produto", example = "12.5")
    @NotNull(message = "Quantidade e obrigatoria")
    @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @Schema(description = "Produto comprado")
    @Valid
    @NotNull(message = "Produto e obrigatorio")
    private ProdutoDTO produto;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
