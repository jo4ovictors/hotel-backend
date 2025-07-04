package br.edu.ifmg.hotelbao.repository;

import br.edu.ifmg.hotelbao.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("DELETE FROM Address u WHERE u.id <> :idToKeep")
    void deleteAllExcept(@Param("idToKeep") Long idToKeep);

}
