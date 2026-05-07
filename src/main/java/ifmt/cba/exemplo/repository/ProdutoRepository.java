package ifmt.cba.exemplo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ifmt.cba.exemplo.entity.GrupoAlimentar;
import ifmt.cba.exemplo.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

    Produto findByNomeIgnoreCaseStartingWith(String nome);
    List<Produto> findByGrupoAlimentar(GrupoAlimentar grupoAlimentar);

    @Query("select p from Produto p where p.estoque < p.estoqueMinimo")
    List<Produto> findByEstoqueMenorEstoqueMinimo();

}
