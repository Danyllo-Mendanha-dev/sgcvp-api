package ifmt.cba.sgcvp.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "categoria_produto")
// Representa uma categoria usada para classificar produtos.
public class CategoriaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name = "nome", length = 80, nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "percentual_desconto", precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    // Valida os campos obrigatorios e percentuais da categoria.
    public String validar() {
        String retorno = "";

        if (this.nome == null || this.nome.length() < 3) {
            retorno += "Nome nao valido";
        }

        if (this.descricao != null && this.descricao.length() > 255) {
            retorno += "Descricao nao valida";
        }

        if (this.percentualComissao != null && this.percentualComissao.compareTo(BigDecimal.ZERO) < 0) {
            retorno += "Percentual de comissao invalido";
        }

        if (this.percentualDesconto != null && this.percentualDesconto.compareTo(BigDecimal.ZERO) < 0) {
            retorno += "Percentual de desconto invalido";
        }

        return retorno;
    }
}
