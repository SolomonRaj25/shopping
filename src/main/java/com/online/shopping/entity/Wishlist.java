package com.online.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wishlist {

    @Id
    private Long productID;
    private String productName;
    private String price;
    private String colour;
    private String size;
    private String productType;
    private String genderSelection;
    private String brand;
    private String imageFile;
    private String userName;
}
