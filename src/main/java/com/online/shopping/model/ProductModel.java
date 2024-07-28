package com.online.shopping.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    @NotNull(message = "Product Name should be entered")
    private String productName;
    @NotNull(message = "Price should be entered")
    private String price;
    @NotNull(message = "Colour should be entered")
    private String colour;
    @NotNull(message = "Size should be entered")
    private String size;
    @NotNull(message = "Product Type should be entered")
    private String productType;
    @NotNull(message = "Gender should be entered")
    private String genderSelection;
    @NotNull(message = "Brand should be entered")
    private String brand;
    @NotNull(message = "Image file is missing")
    private MultipartFile imageFile;

    private String keyword;

}
