package ifmt.cba.sgcvp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.sgcvp.entity.Cliente;
import ifmt.cba.sgcvp.entity.PedidoVenda;

// Acessa dados persistidos de pedidos de venda.
public interface PedidoVendaRepository extends JpaRepository<PedidoVenda, Integer> {

    List<PedidoVenda> findByCliente(Cliente cliente);
    List<PedidoVenda> findByStatusIgnoreCase(String status);
}
