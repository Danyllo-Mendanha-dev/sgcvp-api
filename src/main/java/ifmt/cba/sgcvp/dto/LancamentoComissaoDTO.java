package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class LancamentoComissaoDTO {

    private int codigo;

    @NotNull(message = "Data de lancamento e obrigatoria")
    private LocalDate dataLancamento;

    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "Status e obrigatorio")
    @Size(max = 40, message = "Status deve possuir no maximo 40 caracteres")
    private String status;

    @NotNull(message = "Promotor e obrigatorio")
    private PromotorDTO promotor;

    @NotNull(message = "Pedido de venda e obrigatorio")
    private PedidoVendaDTO pedidoVenda;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
