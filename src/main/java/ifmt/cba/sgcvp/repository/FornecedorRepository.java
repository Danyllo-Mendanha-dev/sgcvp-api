package ifmt.cba.sgcvp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.sgcvp.entity.Fornecedor;

// Acessa dados persistidos de fornecedores.
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Fornecedor findByCNPJ(String CNPJ);
    Fornecedor findByRazaoSocialIgnoreCaseStartingWith(String razaoSocial);
}
