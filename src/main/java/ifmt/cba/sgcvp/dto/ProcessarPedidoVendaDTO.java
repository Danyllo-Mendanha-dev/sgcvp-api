package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados necessarios para processar um pedido de venda.")
public class ProcessarPedidoVendaDTO {

    @Schema(description = "Percentual de comissao aplicado ao processamento do pedido", example = "5.00")
    @NotNull(message = "Percentual de comissao e obrigatorio")
    @DecimalMin(value = "0.01", message = "Percentual de comissao deve ser maior que zero")
    private BigDecimal percentualComissao;
}
