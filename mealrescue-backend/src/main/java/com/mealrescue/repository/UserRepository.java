package com.mealrescue.repository;

import com.mealrescue.model.AccountStatus;
import java.util.List;
import com.mealrescue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByStatusOrderByCreatedAtAsc(AccountStatus status);
}
