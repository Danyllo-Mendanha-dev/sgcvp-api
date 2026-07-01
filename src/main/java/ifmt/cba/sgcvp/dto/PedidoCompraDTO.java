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
@Schema(description = "Pedido de compra realizado junto a um fornecedor.")
// Transporta dados de pedido de compra pela API.
public class PedidoCompraDTO {

    @Schema(description = "Codigo identificador do pedido de compra", example = "1")
    private int codigo;

    @Schema(description = "Data de entrada do pedido de compra", example = "2026-06-30")
    @NotNull(message = "Data de entrada e obrigatoria")
    private LocalDate dataEntrada;

    @Schema(description = "Numero da nota fiscal do pedido de compra", example = "NF-2026-0001")
    @NotBlank(message = "Numero da nota fiscal e obrigatorio")
    @Size(max = 60, message = "Numero da nota fiscal deve possuir no maximo 60 caracteres")
    private String numNotaFiscal;

    @Schema(description = "Status atual do pedido de compra", example = "SOLICITADO")
    @NotBlank(message = "Status e obrigatorio")
    @Size(max = 40, message = "Status deve possuir no maximo 40 caracteres")
    private String status;

    @Schema(description = "Fornecedor vinculado ao pedido de compra")
    @NotNull(message = "Fornecedor e obrigatorio")
    private FornecedorDTO fornecedor;

    @Schema(description = "Itens do pedido de compra")
    @Valid
    @NotEmpty(message = "Pedido deve possuir ao menos um item")
    private List<ItemPedidoCompraDTO> listaItemPedidoCompra = new ArrayList<ItemPedidoCompraDTO>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
