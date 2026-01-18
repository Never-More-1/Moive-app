package movieApp.repository;

import jakarta.transaction.Transactional;
import movieApp.model.Security; // Импортируйте ваш класс
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityRepository extends JpaRepository<Security, Integer> {
    boolean existsByLogin(String login);

    @Query(nativeQuery = true, value = "SELECT * FROM security WHERE role = :roleParam")
    List<Security> customFindByRole(String roleParam);

    Optional<Security> getByLogin(String login); // Этот метод есть

    // Добавьте этот метод если его нет
    Optional<Security> findByLogin(String login); // Spring Data может создать его автоматически

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE security SET role = 'ADMIN' WHERE user_id = :userId")
    int setAdminRoleByUserId(Integer userId);
}