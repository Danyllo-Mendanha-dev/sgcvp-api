package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;

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
public class ProcessarPedidoVendaDTO {

    @NotNull(message = "Percentual de comissao e obrigatorio")
    @DecimalMin(value = "0.01", message = "Percentual de comissao deve ser maior que zero")
    private BigDecimal percentualComissao;
}
