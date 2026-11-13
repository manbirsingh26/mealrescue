package com.mealrescue.repository;

import com.mealrescue.model.AccountStatus;
import java.util.List;
import com.mealrescue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByStatusOrderByCreatedAtAsc(AccountStatus status);
    List<User> findAllByOrderByCreatedAtDesc();

    @Query("select u.role, u.status, count(u) from User u group by u.role, u.status")
    List<Object[]> countByRoleAndStatus();
}
