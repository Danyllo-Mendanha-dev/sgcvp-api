package ifmt.cba.exemplo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.exemplo.entity.Bairro;

public interface BairroRepository extends JpaRepository<Bairro, Integer>{

    Bairro findByNomeIgnoreCaseStartingWith(String nome);

}
