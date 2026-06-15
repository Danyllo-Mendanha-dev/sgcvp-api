package ifmt.cba.sgcvp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ifmt.cba.sgcvp.entity.LancamentoComissao;
import ifmt.cba.sgcvp.entity.PedidoVenda;

public interface LancamentoComissaoRepository extends JpaRepository<LancamentoComissao, Integer> {

    LancamentoComissao findByPedidoVenda(PedidoVenda pedidoVenda);
    List<LancamentoComissao> findByStatusIgnoreCaseStartingWith(String status);

    @Query("select l from LancamentoComissao l "
            + "where upper(l.status) = upper(:status) "
            + "and l.dataLancamento between :dataInicial and :dataFinal "
            + "and l.promotor.codigo = :codigoPromotor "
            + "order by l.dataLancamento, l.codigo")
    List<LancamentoComissao> findByStatusDataLancamentoAndPromotor(
            @Param("status") String status,
            @Param("dataInicial") LocalDate dataInicial,
            @Param("dataFinal") LocalDate dataFinal,
            @Param("codigoPromotor") int codigoPromotor);
}
