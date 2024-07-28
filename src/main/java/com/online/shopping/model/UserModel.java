package com.online.shopping.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @NotNull(message = "Name must required")
    private String fullName;
    @NotNull(message = "Enter valid email address")
    @Email()
    @Column(unique = true)
    private String email;
    @NotNull(message = "Password must required")
    private String password;
    @NotBlank(message = "Enter valid phone number")
    @Size(min = 10, max = 10)
    private String contactNo;
    @NotBlank(message = "Please provide your date of birth")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private String dateOfBirth;
    @NotNull(message = "Select appropriate gender type")
    private String gender;
    @NotNull(message = "Role must required")
    private String role;
}
