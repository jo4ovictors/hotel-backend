package br.edu.ifmg.hotelbao.repositories;

import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.projections.UserDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.authority = :role")
    Page<User> findByRole(@Param("role") String role, Pageable pageable);

    @Query(nativeQuery = true,
            value = """
                   SELECT u.login as username,
                          u.password,
                          r.id as roleId,
                          r.authority
                   
                   FROM tb_user u
                   INNER JOIN tb_user_role ur ON u.id = ur.user_id
                   INNER JOIN tb_role r ON r.id = ur.role_id
                   WHERE u.login = :username
                   """
    )
    List<UserDetailsProjection> searchUserAndRoleByLogin(String username);

}
