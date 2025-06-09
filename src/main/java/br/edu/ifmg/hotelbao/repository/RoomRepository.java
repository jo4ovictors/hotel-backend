package br.edu.ifmg.hotelbao.repository;

import br.edu.ifmg.hotelbao.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
