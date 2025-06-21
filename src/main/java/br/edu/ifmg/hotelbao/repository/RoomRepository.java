package br.edu.ifmg.hotelbao.repository;

import br.edu.ifmg.hotelbao.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.isActive = true")
    List<Room> findAll();

    @Query("SELECT r FROM Room r WHERE r.isActive = true AND r.id = :id")
    Optional<Room> findActiveById(Long id);
}
