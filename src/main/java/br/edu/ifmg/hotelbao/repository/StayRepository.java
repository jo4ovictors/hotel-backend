package br.edu.ifmg.hotelbao.repository;

import br.edu.ifmg.hotelbao.entities.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {

    List<Stay> findByUserId(Long userId);

    Optional<Stay> findTopByUserIdOrderByPriceDesc(Long userId);
    Optional<Stay> findTopByUserIdOrderByPriceAsc(Long userId);

    @Query(nativeQuery = true,
            value = """
                        SELECT SUM(s.price)
                        FROM tb_stay s
                        WHERE s.user_id = :userId
                    """
    )
    BigDecimal findTotalAmountSpentByUser(Long userId);
}
