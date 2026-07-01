package ifmt.cba.sgcvp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ifmt.cba.sgcvp.entity.CategoriaProduto;
import ifmt.cba.sgcvp.entity.Produto;

// Acessa dados persistidos de produtos.
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    Produto findByNomeIgnoreCaseStartingWith(String nome);
    List<Produto> findByCategoriaProduto(CategoriaProduto categoriaProduto);
    List<Produto> findByCategoriaProdutoOrderByNomeAsc(CategoriaProduto categoriaProduto);
    List<Produto> findByCategoriaProdutoOrderByQuantidadeEstoqueDesc(CategoriaProduto categoriaProduto);

    @Query("select p from Produto p where p.quantidadeEstoque < p.estoqueMinimo order by p.nome")
    List<Produto> findByQuantidadeEstoqueAbaixoEstoqueMinimo();
}
