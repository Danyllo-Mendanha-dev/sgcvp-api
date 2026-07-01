package ifmt.cba.sgcvp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.sgcvp.entity.CategoriaProduto;

// Acessa dados persistidos de categorias de produto.
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Integer> {

    CategoriaProduto findByNomeIgnoreCaseStartingWith(String nome);
    List<CategoriaProduto> findAllByOrderByNomeAsc();
}
