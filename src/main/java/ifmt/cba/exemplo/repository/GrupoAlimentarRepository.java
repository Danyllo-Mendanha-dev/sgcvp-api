package ifmt.cba.exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.exemplo.entity.GrupoAlimentar;

public interface GrupoAlimentarRepository extends JpaRepository<GrupoAlimentar, Integer>{

    GrupoAlimentar findByNomeIgnoreCaseStartingWith(String nome);

}