package com.online.shopping.service;

import com.online.shopping.entity.UserEntity;
import com.online.shopping.entity.VerificationToken;
import com.online.shopping.exception.customUserNotFoundException;
import com.online.shopping.model.PasswordModel;
import com.online.shopping.model.UserModel;
import com.online.shopping.repository.UserRepository;
import com.online.shopping.repository.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity addUser(UserModel user) {
        UserEntity newUser = UserEntity.builder().fullName(user.getFullName())
                .email(user.getEmail()).password(passwordEncoder.encode(user.getPassword()))
                .contactNo(user.getContactNo()).dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender()).role(user.getRole())
                .build();
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public void removeUser(String email) throws customUserNotFoundException {
        UserEntity user = userRepository.findUserByEmail(email);
        if (user!=null) {
            VerificationToken token = verificationRepository.findByUser(user);
            if (token!=null) {
                verificationRepository.delete(token);
            }
            userRepository.delete(user);
        } else {
            throw new customUserNotFoundException("User not found");
        }
    }

    @Override
    public UserEntity updateUser(String email, UserModel userModel) {
        UserEntity user = userRepository.findUserByEmail(email);
        if (Objects.nonNull(userModel.getFullName()) && !"".equalsIgnoreCase(userModel.getFullName())) {
            user.setFullName(userModel.getFullName());
        }
        if (Objects.nonNull(userModel.getContactNo()) && !"".equalsIgnoreCase(userModel.getContactNo())) {
            user.setContactNo(userModel.getContactNo());
        }
        if (Objects.nonNull(userModel.getGender()) && !"".equalsIgnoreCase(userModel.getGender())) {
            user.setGender(userModel.getGender());
        }
        if (Objects.nonNull(userModel.getDateOfBirth()) && !"".equalsIgnoreCase(userModel.getDateOfBirth())) {
            user.setDateOfBirth(userModel.getDateOfBirth());
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationToken(UserEntity user, String token) {
        VerificationToken verificationToken = new VerificationToken(user,token);
        verificationRepository.save(verificationToken);
    }

    @Override
    public String verifyToken(String token) {
        Optional<VerificationToken> verificationToken = verificationRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return "Invalid Token";
        }
        UserEntity user = verificationToken.get().getUser();
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.get().getExpirationTime().getTime()-calendar.getTime().getTime())<0) {
            return "Token Expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "User Verified Successfully";
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findUserByEmail(email));
    }

    @Override
    public UserEntity resendVerificationToken(String token) {
        Optional<VerificationToken> verificationToken = verificationRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            throw new RuntimeException("Invalid Link");
        }
        UserEntity user = verificationToken.get().getUser();
        verificationRepository.delete(verificationToken.get());
        return user;
    }

    @Override
    public VerificationToken createPasswordResetToken(String token, Optional<UserEntity> user) {
        VerificationToken oldToken = verificationRepository.findByUser(user.get());
        verificationRepository.delete(oldToken);
        VerificationToken newToken = new VerificationToken(user.get(),token);
        verificationRepository.save(newToken);
        return newToken;
    }

    @Override
    public boolean checkOldPasswordIsValid(UserEntity user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public void changePassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> validateResetToken(String token) {
        Optional<VerificationToken> verificationToken = verificationRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return Optional.empty();
        }
        UserEntity user = verificationToken.get().getUser();
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.get().getExpirationTime().getTime()-calendar.getTime().getTime())<0) {
            return Optional.empty();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void setNewPassword(UserEntity user, PasswordModel passwordModel) throws customUserNotFoundException {
        UserEntity isUserPresent = userRepository.findUserByEmail(user.getEmail());
        if (Optional.of(isUserPresent).isEmpty()) {
            throw new customUserNotFoundException("User not found with "+passwordModel.getEmail());
        }
        user.setPassword(passwordEncoder.encode(passwordModel.getPassword()));
        userRepository.save(user);
    }

}
