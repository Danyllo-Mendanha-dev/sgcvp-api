package ifmt.cba.sgcvp.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "produto")
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

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(name = "estoque_minimo", nullable = false)
    private int estoqueMinimo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_produto", nullable = false)
    private CategoriaProduto categoriaProduto;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

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

        if (this.quantidadeEstoque < 0) {
            retorno += "Quantidade em estoque invalida";
        }

        if (this.estoqueMinimo < 0) {
            retorno += "Estoque minimo invalido";
        }

        if (this.categoriaProduto == null || this.categoriaProduto.getCodigo() <= 0) {
            retorno += "Categoria do produto invalida";
        }

        return retorno;
    }
}
