package ifmt.cba.sgcvp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "listaItemPedidoCompra")
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "pedido_compra")
// Representa uma compra realizada junto a fornecedor.
public class PedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private int codigo;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "num_nota_fiscal", length = 60, nullable = false)
    private String numNotaFiscal;

    @Column(name = "status", length = 40, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_fornecedor", nullable = false)
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "pedidoCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedidoCompra> listaItemPedidoCompra = new ArrayList<ItemPedidoCompra>();

    // Valida cabecalho, fornecedor e itens da compra.
    public String validar() {
        String retorno = "";

        if (this.dataEntrada == null) {
            retorno += "Data de entrada invalida";
        }

        if (this.numNotaFiscal == null || this.numNotaFiscal.length() < 1 || this.numNotaFiscal.length() > 60) {
            retorno += "Numero da nota fiscal invalido";
        }

        if (this.status == null || this.status.length() < 3 || this.status.length() > 40) {
            retorno += "Status invalido";
        }

        if (this.fornecedor == null || this.fornecedor.getCodigo() <= 0) {
            retorno += "Fornecedor invalido";
        }

        if (this.listaItemPedidoCompra == null || this.listaItemPedidoCompra.isEmpty()) {
            retorno += "Itens do pedido invalidos";
        }

        return retorno;
    }
}
