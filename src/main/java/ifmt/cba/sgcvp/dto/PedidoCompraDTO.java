package ifmt.cba.sgcvp.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class PedidoCompraDTO {

    private int codigo;

    @NotNull(message = "Data de entrada e obrigatoria")
    private LocalDate dataEntrada;

    @NotBlank(message = "Numero da nota fiscal e obrigatorio")
    @Size(max = 60, message = "Numero da nota fiscal deve possuir no maximo 60 caracteres")
    private String numNotaFiscal;

    @NotBlank(message = "Status e obrigatorio")
    @Size(max = 40, message = "Status deve possuir no maximo 40 caracteres")
    private String status;

    @NotNull(message = "Fornecedor e obrigatorio")
    private FornecedorDTO fornecedor;

    @Valid
    @NotEmpty(message = "Pedido deve possuir ao menos um item")
    private List<ItemPedidoCompraDTO> listaItemPedidoCompra = new ArrayList<ItemPedidoCompraDTO>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
