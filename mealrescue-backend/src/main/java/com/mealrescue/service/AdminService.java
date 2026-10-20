package com.mealrescue.service;

import com.mealrescue.dto.PendingAccountResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.User;
import com.mealrescue.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PendingAccountResponse> getPendingAccounts() {
        return userRepository.findByStatusOrderByCreatedAtAsc(AccountStatus.PENDING).stream()
                .map(PendingAccountResponse::from)
                .toList();
    }

    @Transactional
    public void approve(Long id) {
        User user = findPending(id);
        user.setStatus(AccountStatus.ACTIVE);
        user.setRejectionReason(null);
    }

    @Transactional
    public void reject(Long id, String reason) {
        User user = findPending(id);
        user.setStatus(AccountStatus.REJECTED);
        user.setRejectionReason(reason.trim());
    }

    private User findPending(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
        if (user.getStatus() != AccountStatus.PENDING) {
            throw new ApiException(HttpStatus.CONFLICT, "This account is not waiting for verification.");
        }
        return user;
    }
}

