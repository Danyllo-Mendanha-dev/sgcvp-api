package ifmt.cba.sgcvp.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ifmt.cba.sgcvp.dto.ClienteValorVendidoDTO;
import ifmt.cba.sgcvp.entity.Cliente;
import ifmt.cba.sgcvp.entity.Promotor;

// Acessa dados persistidos de clientes.
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Cliente findByCNPJ(String CNPJ);
    Cliente findByRazaoSocialIgnoreCaseStartingWith(String razaoSocial);
    List<Cliente> findByPromotor(Promotor promotor);

    @Query("select new ifmt.cba.sgcvp.dto.ClienteValorVendidoDTO(c.codigo, c.razaoSocial, c.nomeFantasia, c.CNPJ, sum(i.quantidade * i.valorUnitario)) "
            + "from PedidoVenda p join p.cliente c join p.listaItemPedidoVenda i "
            + "where p.promotor.codigo = :codigoPromotor "
            + "and p.dataPedido between :dataInicial and :dataFinal "
            + "and upper(p.status) = 'PROCESSADO' "
            + "group by c.codigo, c.razaoSocial, c.nomeFantasia, c.CNPJ "
            + "order by sum(i.quantidade * i.valorUnitario) desc")
    List<ClienteValorVendidoDTO> findClientesPorPromotorOrderByValorVendidoDesc(
            @Param("codigoPromotor") int codigoPromotor,
            @Param("dataInicial") LocalDate dataInicial,
            @Param("dataFinal") LocalDate dataFinal);
}
