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
@Schema(description = "Item que compoe um pedido de venda.")
// Transporta item de pedido de venda pela API.
public class ItemPedidoVendaDTO {

    @Schema(description = "Codigo identificador do item do pedido de venda", example = "1")
    private int codigo;

    @Schema(description = "Quantidade vendida do produto", example = "3.0")
    @NotNull(message = "Quantidade e obrigatoria")
    @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @Schema(description = "Valor unitario praticado na venda", example = "24.90")
    @NotNull(message = "Valor unitario e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor unitario deve ser maior que zero")
    private BigDecimal valorUnitario;

    @Schema(description = "Produto vendido")
    @Valid
    @NotNull(message = "Produto e obrigatorio")
    private ProdutoDTO produto;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
