package com.online.shopping.repository;

import com.online.shopping.entity.UserEntity;
import com.online.shopping.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    VerificationToken findByUser(UserEntity user);
}
