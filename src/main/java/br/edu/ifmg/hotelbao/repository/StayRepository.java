package br.edu.ifmg.hotelbao.repository;

import br.edu.ifmg.hotelbao.dtos.StayItemDTO;
import br.edu.ifmg.hotelbao.entities.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {


    List<Stay> findByUserId(Long userId);

    Optional<Stay> findTopByUserIdOrderByPriceDesc(Long userId);
    Optional<Stay> findTopByUserIdOrderByPriceAsc(Long userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Stay s " +
            "WHERE s.room.id = :roomId " +
            "AND (:stayId IS NULL OR s.id <> :stayId) " +
            "AND ( (s.checkIn <= :checkOut) AND (s.checkOut >= :checkIn) )")
    boolean isRoomOccupied(@Param("roomId") Long roomId,
                           @Param("checkIn") LocalDate checkIn,
                           @Param("checkOut") LocalDate checkOut,
                           @Param("stayId") Long stayId);


    @Query(nativeQuery = true,
            value = """
                        SELECT SUM(s.price)
                        FROM tb_stay s
                        WHERE s.user_id = :userId
                    """
    )
    BigDecimal findTotalAmountSpentByUser(Long userId);

    @Query("""
    SELECT new br.edu.ifmg.hotelbao.dtos.StayItemDTO(
        r.description,
        s.price,
        s.checkIn,
        s.checkOut
    )
    FROM Stay s
    JOIN s.room r
    WHERE s.user.id = :userId
    """)
    List<StayItemDTO> findStayItemsByUserId(Long userId);

}
