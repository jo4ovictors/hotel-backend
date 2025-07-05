package br.edu.ifmg.hotelbao.repositories;

import br.edu.ifmg.hotelbao.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
