package ifmt.cba.sgcvp.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "item_pedido_venda")
public class ItemPedidoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_venda")
    private int codigo;

    @Column(name = "quantidade", precision = 12, scale = 3, nullable = false)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorUnitario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoVenda pedidoVenda;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.quantidade == null || this.quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            retorno += "Quantidade invalida";
        }

        if (this.valorUnitario == null || this.valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            retorno += "Valor unitario invalido";
        }

        if (this.produto == null || this.produto.getCodigo() <= 0) {
            retorno += "Produto invalido";
        }

        return retorno;
    }
}
