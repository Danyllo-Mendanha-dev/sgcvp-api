package ifmt.cba.sgcvp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "pedido_venda")
public class PedidoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private int codigo;

    @Column(name = "data_pedido", nullable = false)
    private LocalDate dataPedido;

    @Column(name = "status", length = 40, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_promotor", nullable = false)
    private Promotor promotor;

    @OneToMany(mappedBy = "pedidoVenda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedidoVenda> listaItemPedidoVenda = new ArrayList<ItemPedidoVenda>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.dataPedido == null) {
            retorno += "Data do pedido invalida";
        }

        if (this.status == null || this.status.length() < 3 || this.status.length() > 40) {
            retorno += "Status invalido";
        }

        if (this.cliente == null || this.cliente.getCodigo() <= 0) {
            retorno += "Cliente invalido";
        }

        if (this.promotor == null || this.promotor.getCodigo() <= 0) {
            retorno += "Promotor invalido";
        }

        if (this.listaItemPedidoVenda == null || this.listaItemPedidoVenda.isEmpty()) {
            retorno += "Itens do pedido invalidos";
        }

        return retorno;
    }
}
