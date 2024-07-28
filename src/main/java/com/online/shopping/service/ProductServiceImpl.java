package com.online.shopping.service;

import com.online.shopping.entity.Product;
import com.online.shopping.model.ProductModel;
import com.online.shopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addProduct(ProductModel product) {
        MultipartFile image = product.getImageFile();
        String fileName = image.getOriginalFilename();
        try {
            String upload = "public/Images/";
            Path uploadPath = Paths.get(upload);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(upload+fileName),StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Product newProduct = Product.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .colour(product.getColour())
                .size(product.getSize())
                .productType(product.getProductType())
                .genderSelection(product.getGenderSelection())
                .brand(product.getBrand())
                .imageFile(fileName)
                .build();
        productRepository.save(newProduct);
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (Optional.of(products).isEmpty()) {
            return new ArrayList<Product>();
        }
        Collections.shuffle(products);
        return products;
    }

    @Override
    public void deleteProductById(long productID) {
        productRepository.deleteById(productID);
    }

    @Override
    public List<Product> getProductByFilter(String keyword) {
        if (keyword==null) {
            return new ArrayList<>();
        }
        String[] k = keyword.split("\\s+");
        String k1 = "",k2 = "";
        for ( String key : k) {
            k2=k1;
            k1=key;
        }
        System.out.println("K1: "+k1+",K2: "+k2);
        return productRepository.search(k1,k2);
    }

    @Override
    public Product getProductsById(long id) {
         return productRepository.findById(id).get();
    }

    @Override
    public void updateProduct(long productID, Product product) {
        Product productInfo=productRepository.findById(productID).get();
        if (Objects.nonNull(product.getProductName())&&!"".equalsIgnoreCase(product.getProductName())) {
            productInfo.setProductName(product.getProductName());
        }
        if (Objects.nonNull(product.getPrice())&&!"".equalsIgnoreCase(product.getPrice())) {
            productInfo.setPrice(product.getPrice());
        }
        if (Objects.nonNull(product.getColour())&&!"".equalsIgnoreCase(product.getColour())) {
            productInfo.setColour(product.getColour());
        }
        if (Objects.nonNull(product.getSize())&&!"".equalsIgnoreCase(product.getSize())) {
            productInfo.setSize(product.getSize());
        }
        if (Objects.nonNull(product.getProductType())&&!"".equalsIgnoreCase(product.getProductType())) {
            productInfo.setProductType(product.getProductType());
        }
        if (Objects.nonNull(product.getGenderSelection())&&!"".equalsIgnoreCase(product.getGenderSelection())) {
            productInfo.setGenderSelection(product.getGenderSelection());
        }
        if (Objects.nonNull(product.getBrand())&&!"".equalsIgnoreCase(product.getBrand())) {
            productInfo.setBrand(product.getBrand());
        }
        if (Objects.nonNull(product.getImageFile())&&!"".equalsIgnoreCase(product.getImageFile())) {
            productInfo.setImageFile(product.getImageFile());
        }
        productRepository.save(productInfo);
    }

}
