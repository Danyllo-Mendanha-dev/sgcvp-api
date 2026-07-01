package ifmt.cba.sgcvp.dto;

import java.math.BigDecimal;

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
@Schema(description = "Resumo de cliente com valor total vendido em um periodo.")
// Transporta o resumo de vendas por cliente.
public class ClienteValorVendidoDTO {

    @Schema(description = "Codigo identificador do cliente", example = "1")
    private int codigo;
    @Schema(description = "Razao social do cliente", example = "Mercado Central Ltda")
    private String razaoSocial;
    @Schema(description = "Nome fantasia do cliente", example = "Mercado Central")
    private String nomeFantasia;
    @Schema(description = "CNPJ do cliente com 14 digitos, sem pontuacao", example = "12345678000199")
    private String CNPJ;
    @Schema(description = "Valor total vendido para o cliente no periodo informado", example = "1520.75")
    private BigDecimal valorVendido;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
