package com.okeyifee.hrservice.repositories;

import com.okeyifee.hrservice.entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    Products findByName(String name);

    @Query(value="SELECT * FROM products u WHERE is_available = true", nativeQuery = true)
    List<Products> getAllAvailableProducts();

    @Query(value="SELECT * FROM products u WHERE is_available = true AND brand_name != ?1", nativeQuery = true)
    List<Products> getAllValidProducts(String brandName);
}
