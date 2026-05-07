package ifmt.cba.exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.exemplo.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

    Cliente findByNomeIgnoreCaseStartingWith(String nome);

    Cliente findByCPF(String cpf);

}
