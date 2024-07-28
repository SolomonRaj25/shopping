package com.online.shopping.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "UserInformation")
public class UserEntity {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @Column(name = "UserId")
    private Long userId;
    @Column(name = "FullName")
    private String fullName;
    @Column(name = "Email")
    private String email;
    @Column(name = "Password")
    private String password;
    @Column(name = "ContactNumber")
    private String contactNo;
    @Column(name = "DateOfBirth")
    private String dateOfBirth;
    @Column(name = "Gender")
    private String gender;
    @Column(name = "Role")
    private String role;
    private boolean enabled = false;
}
