package ifmt.cba.sgcvp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "lancamento_comissao")
// Representa uma comissao gerada a partir de uma venda.
public class LancamentoComissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comissao")
    private int codigo;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "valor", precision = 12, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "status", length = 40, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_promotor", nullable = false)
    private Promotor promotor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoVenda pedidoVenda;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    // Valida dados obrigatorios do lancamento de comissao.
    public String validar() {
        String retorno = "";

        if (this.dataLancamento == null) {
            retorno += "Data de lancamento invalida";
        }

        if (this.valor == null || this.valor.compareTo(BigDecimal.ZERO) <= 0) {
            retorno += "Valor invalido";
        }

        if (this.status == null || this.status.length() < 3 || this.status.length() > 40) {
            retorno += "Status invalido";
        }

        if (this.promotor == null || this.promotor.getCodigo() <= 0) {
            retorno += "Promotor invalido";
        }

        if (this.pedidoVenda == null || this.pedidoVenda.getCodigo() <= 0) {
            retorno += "Pedido de venda invalido";
        }

        return retorno;
    }
}
