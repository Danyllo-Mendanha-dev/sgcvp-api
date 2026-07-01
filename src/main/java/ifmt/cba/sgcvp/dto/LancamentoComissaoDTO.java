package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Lancamento financeiro de comissao de promotor.")
public class LancamentoComissaoDTO {

    @Schema(description = "Codigo identificador do lancamento de comissao", example = "1")
    private int codigo;

    @Schema(description = "Data em que a comissao foi lancada", example = "2026-06-30")
    @NotNull(message = "Data de lancamento e obrigatoria")
    private LocalDate dataLancamento;

    @Schema(description = "Valor da comissao lancada", example = "185.50")
    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @Schema(description = "Status do lancamento de comissao", example = "LANCADA")
    @NotBlank(message = "Status e obrigatorio")
    @Size(max = 40, message = "Status deve possuir no maximo 40 caracteres")
    private String status;

    @Schema(description = "Promotor relacionado ao lancamento")
    @NotNull(message = "Promotor e obrigatorio")
    private PromotorDTO promotor;

    @Schema(description = "Pedido de venda que originou a comissao")
    @NotNull(message = "Pedido de venda e obrigatorio")
    private PedidoVendaDTO pedidoVenda;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
