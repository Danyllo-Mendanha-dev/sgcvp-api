package ifmt.cba.sgcvp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.sgcvp.entity.Promotor;

public interface PromotorRepository extends JpaRepository<Promotor, Integer> {

    Promotor findByNomeIgnoreCaseStartingWith(String nome);
}
