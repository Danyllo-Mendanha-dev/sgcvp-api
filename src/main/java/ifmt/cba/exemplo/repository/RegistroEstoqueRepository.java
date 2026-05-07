package ifmt.cba.exemplo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.exemplo.dto.MovimentoEstoqueDTO;
import ifmt.cba.exemplo.entity.Produto;
import ifmt.cba.exemplo.entity.RegistroEstoque;

public interface RegistroEstoqueRepository extends JpaRepository<RegistroEstoque, Integer>{

    List<RegistroEstoque> findByMovimento(MovimentoEstoqueDTO movimento);

    List<RegistroEstoque> findByMovimentoAndData(MovimentoEstoqueDTO movimento, LocalDate data);

    List<RegistroEstoque> findByProduto(Produto produto);

}