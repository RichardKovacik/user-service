package sk.mvp.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.mvp.user_service.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
