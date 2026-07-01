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
@Schema(description = "Dados cadastrais e de estoque de um produto.")
// Transporta dados cadastrais e de estoque de produto.
public class ProdutoDTO {

    @Schema(description = "Codigo identificador do produto", example = "1")
    private int codigo;
    @Schema(description = "Nome do produto", example = "Cafe torrado 500g")
    private String nome;
    @Schema(description = "Descricao do produto", example = "Cafe torrado e moido em embalagem de 500g")
    private String descricao;
    @Schema(description = "Preco de venda do produto", example = "18.90")
    private BigDecimal precoVenda;
    @Schema(description = "Valor de custo do produto", example = "12.50")
    private BigDecimal valorCusto;
    @Schema(description = "Margem de lucro aplicada ao produto", example = "30.00")
    private BigDecimal margemLucro;
    @Schema(description = "Percentual promocional aplicado ao produto", example = "5.00")
    private BigDecimal percentualPromocao;
    @Schema(description = "Quantidade atual em estoque", example = "120")
    private int quantidadeEstoque;
    @Schema(description = "Quantidade minima recomendada em estoque", example = "20")
    private int estoqueMinimo;
    @Schema(description = "Quantidade maxima recomendada em estoque", example = "200")
    private int quantidadeMaximaEstoque;
    @Schema(description = "Quantidade reservada em pedidos", example = "0")
    private int quantidadeReservadaPedido;
    @Schema(description = "Percentual de comissao do produto", example = "5.00")
    private BigDecimal percentualComissao;
    @Schema(description = "Categoria a qual o produto pertence")
    private CategoriaProdutoDTO categoriaProduto;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
