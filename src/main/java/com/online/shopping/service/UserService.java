package com.online.shopping.service;

import com.online.shopping.entity.UserEntity;
import com.online.shopping.entity.VerificationToken;
import com.online.shopping.exception.customUserNotFoundException;
import com.online.shopping.model.PasswordModel;
import com.online.shopping.model.UserModel;

import java.util.Optional;

public interface UserService {

    UserEntity addUser(UserModel userModel);

    void removeUser(String email) throws customUserNotFoundException;

    UserEntity updateUser(String email, UserModel userModel) throws customUserNotFoundException;

    void saveVerificationToken(UserEntity user, String token);

    String verifyToken(String token);

    Optional<UserEntity> findUserByEmail(String email);

    UserEntity resendVerificationToken(String token);

    VerificationToken createPasswordResetToken(String token, Optional<UserEntity> user);

    void setNewPassword(UserEntity user, PasswordModel passwordModel) throws customUserNotFoundException;

    boolean checkOldPasswordIsValid(UserEntity user, String newPassword);

    void changePassword(UserEntity user, String newPassword);

    Optional<UserEntity> validateResetToken(String token);

}
