package com.online.shopping.service;

import com.online.shopping.entity.Product;
import com.online.shopping.model.ProductModel;

import java.util.List;

public interface ProductService {
    void addProduct(ProductModel product);

    List<Product> getAllProducts();

    void deleteProductById(long productID);

    List<Product> getProductByFilter(String keyword);

    Product getProductsById(long id);

    void updateProduct(long productID, Product product);
}