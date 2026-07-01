package ifmt.cba.sgcvp.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "Pedido de venda realizado para um cliente.")
public class PedidoVendaDTO {

    @Schema(description = "Codigo identificador do pedido de venda", example = "1")
    private int codigo;

    @Schema(description = "Data do pedido de venda", example = "2026-06-30")
    @NotNull(message = "Data do pedido e obrigatoria")
    private LocalDate dataPedido;

    @Schema(description = "Status atual do pedido de venda", example = "SOLICITADO")
    @NotBlank(message = "Status e obrigatorio")
    @Size(max = 40, message = "Status deve possuir no maximo 40 caracteres")
    private String status;

    @Schema(description = "Cliente vinculado ao pedido de venda")
    @NotNull(message = "Cliente e obrigatorio")
    private ClienteDTO cliente;

    @Schema(description = "Promotor responsavel pelo pedido de venda")
    @NotNull(message = "Promotor e obrigatorio")
    private PromotorDTO promotor;

    @Schema(description = "Itens do pedido de venda")
    @Valid
    @NotEmpty(message = "Pedido deve possuir ao menos um item")
    private List<ItemPedidoVendaDTO> listaItemPedidoVenda = new ArrayList<ItemPedidoVendaDTO>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
