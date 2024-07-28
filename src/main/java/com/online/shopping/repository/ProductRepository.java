package com.online.shopping.repository;

import com.online.shopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("SELECT product FROM Product product where concat(product.productName,product.brand) ilike %?1% and concat(product.productName,product.brand) ilike %?2%")
    List<Product> search(String keyword, String keyword1);

    @Query(value = "Select p from Product p where productName=?1 or brand=?1")
    List<Product> searchProduct(String keyword);

    List<Product> findByProductNameLikeIgnoreCase(String keyword);

}
