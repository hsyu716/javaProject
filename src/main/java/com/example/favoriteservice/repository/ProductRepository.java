package com.example.favoriteservice.repository;

import com.example.favoriteservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByProductNameContaining(String productName);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByFeeRateBetween(BigDecimal minFeeRate, BigDecimal maxFeeRate);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR p.productName LIKE %:keyword%) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minFeeRate IS NULL OR p.feeRate >= :minFeeRate) AND " +
           "(:maxFeeRate IS NULL OR p.feeRate <= :maxFeeRate)")
    List<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minFeeRate") BigDecimal minFeeRate,
        @Param("maxFeeRate") BigDecimal maxFeeRate
    );
    
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findAllOrderByPriceAsc();
    
    @Query("SELECT p FROM Product p ORDER BY p.feeRate ASC")
    List<Product> findAllOrderByFeeRateAsc();
}
