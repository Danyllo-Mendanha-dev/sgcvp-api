package ifmt.cba.sgcvp.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "produto")
// Representa um produto comercializado e controlado em estoque.
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name = "nome", length = 120, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "preco_venda", precision = 12, scale = 2, nullable = false)
    private BigDecimal precoVenda;

    @Column(name = "valor_custo", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorCusto;

    @Column(name = "margem_lucro", precision = 5, scale = 2, nullable = false)
    private BigDecimal margemLucro;

    @Column(name = "percentual_promocao", precision = 5, scale = 2)
    private BigDecimal percentualPromocao;

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(name = "estoque_minimo", nullable = false)
    private int estoqueMinimo;

    @Column(name = "quantidade_maxima_estoque", nullable = false)
    private int quantidadeMaximaEstoque;

    @Column(name = "quantidade_reservada_pedido", nullable = false)
    private int quantidadeReservadaPedido;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_produto", nullable = false)
    private CategoriaProduto categoriaProduto;

    // Valida dados comerciais, estoque e categoria do produto.
    public String validar() {
        String retorno = "";

        if (this.nome == null || this.nome.length() < 3) {
            retorno += "Nome invalido";
        }

        if (this.descricao != null && this.descricao.length() > 255) {
            retorno += "Descricao invalida";
        }

        if (this.precoVenda == null || this.precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
            retorno += "Preco de venda invalido";
        }

        if (this.valorCusto == null || this.valorCusto.compareTo(BigDecimal.ZERO) <= 0) {
            retorno += "Valor de custo invalido";
        }

        if (this.margemLucro == null || this.margemLucro.compareTo(BigDecimal.ZERO) < 0) {
            retorno += "Margem de lucro invalida";
        }

        if (this.percentualPromocao != null && this.percentualPromocao.compareTo(BigDecimal.ZERO) < 0) {
            retorno += "Percentual de promocao invalido";
        }

        if (this.quantidadeEstoque < 0) {
            retorno += "Quantidade em estoque invalida";
        }

        if (this.estoqueMinimo < 0) {
            retorno += "Estoque minimo invalido";
        }

        if (this.quantidadeMaximaEstoque < 0) {
            retorno += "Quantidade maxima em estoque invalida";
        }

        if (this.quantidadeReservadaPedido < 0) {
            retorno += "Quantidade reservada em pedido invalida";
        }

        if (this.percentualComissao != null && this.percentualComissao.compareTo(BigDecimal.ZERO) < 0) {
            retorno += "Percentual de comissao invalido";
        }

        if (this.categoriaProduto == null || this.categoriaProduto.getCodigo() <= 0) {
            retorno += "Categoria do produto invalida";
        }

        return retorno;
    }
}
