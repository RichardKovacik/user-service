package sk.mvp.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.mvp.user_service.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findByFirstName(String username);
    @Query("select u from User u JOIN u.contact c where c.email = :email")
    Optional<User> findByEmail(String email);

}
