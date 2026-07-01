package ifmt.cba.sgcvp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.sgcvp.entity.Fornecedor;
import ifmt.cba.sgcvp.entity.PedidoCompra;

// Acessa dados persistidos de pedidos de compra.
public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Integer> {

    List<PedidoCompra> findByFornecedor(Fornecedor fornecedor);
    PedidoCompra findByNumNotaFiscal(String numNotaFiscal);
}
