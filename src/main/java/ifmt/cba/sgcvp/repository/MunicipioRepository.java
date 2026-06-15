package ifmt.cba.sgcvp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ifmt.cba.sgcvp.entity.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

    Municipio findByNomeIgnoreCaseStartingWith(String nome);

    @Query("select m from Promotor p join p.listaMunicipio m where p.codigo = :codigoPromotor order by m.nome")
    List<Municipio> findMunicipiosByPromotorOrderByNome(@Param("codigoPromotor") int codigoPromotor);
}
