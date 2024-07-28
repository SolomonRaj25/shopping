package com.online.shopping.repository;

import com.online.shopping.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckOutRepository extends JpaRepository<DeliveryAddress,Long> {
    List<DeliveryAddress> findByActualUser(String user);
}
