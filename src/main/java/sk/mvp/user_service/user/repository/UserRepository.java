package sk.mvp.user_service.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.mvp.user_service.entity.Gender;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.projections.UserSummaryProjection;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findByFirstName(String username);
    Optional<User> findByUsername(String username);
    @Query("select u from User u JOIN u.contact c where c.email = :email")
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.enabled = :flag where u.username = :username")
    void setEnabled(String username, boolean flag);

    @Query("select u.tokenVersion from User u where u.username = :username")
    Optional<Integer> getTokenVersion(String username);

    @Modifying
    @Query("update User u set u.tokenVersion = u.tokenVersion + 1 where u.username = :username")
    void incrementTokenVersion(@Param("username") String username);

    User save(User user);

    void delete(User user);

    @Modifying
    @Query("delete from User u " +
            "where u.id in " +
            "(select c.user.id" +
            " from Contact c" +
            " where c.email = :email)")
    void deleteUserByEmail(String email);

    Page<UserSummaryProjection> findAllProjectedBy(Pageable pageable);
    Page<UserSummaryProjection> findAllByGender(Gender gender, Pageable pageable);
}
