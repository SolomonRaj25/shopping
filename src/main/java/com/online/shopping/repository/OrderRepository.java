package com.online.shopping.repository;

import com.online.shopping.entity.DeliveryInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<DeliveryInformation,Long> {
}
