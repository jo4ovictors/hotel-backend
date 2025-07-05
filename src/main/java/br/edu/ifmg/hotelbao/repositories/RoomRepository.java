package br.edu.ifmg.hotelbao.repositories;

import br.edu.ifmg.hotelbao.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findByIsActiveTrue(Pageable pageable);

    Optional<Room> findByIdAndIsActiveTrue(Long id);

}
