package com.example.favoriteservice.repository;

import com.example.favoriteservice.entity.LikeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeListRepository extends JpaRepository<LikeList, Long> {
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.user.userId = :userId")
    List<LikeList> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.user.userId = :userId ORDER BY ll.createdAt DESC")
    List<LikeList> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.product.no = :productNo")
    List<LikeList> findByProductNo(@Param("productNo") Long productNo);
    
    @Modifying
    @Query("DELETE FROM LikeList ll WHERE ll.product.no = :productNo")
    void deleteByProductNo(@Param("productNo") Long productNo);
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.account = :account")
    List<LikeList> findByAccount(@Param("account") String account);
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.user.userId = :userId AND ll.product.no = :productNo")
    List<LikeList> findByUserIdAndProductNo(@Param("userId") String userId, @Param("productNo") Long productNo);
    
    @Query("SELECT COUNT(ll) FROM LikeList ll WHERE ll.user.userId = :userId")
    long countByUserId(@Param("userId") String userId);
    
    @Query("SELECT SUM(ll.totalAmount) FROM LikeList ll WHERE ll.user.userId = :userId")
    Double getTotalInvestmentByUserId(@Param("userId") String userId);
    
    @Query("SELECT SUM(ll.totalFee) FROM LikeList ll WHERE ll.user.userId = :userId")
    Double getTotalFeesByUserId(@Param("userId") String userId);
    
    @Query("SELECT ll FROM LikeList ll WHERE ll.user.userId = :userId ORDER BY ll.createdAt DESC")
    List<LikeList> findByUserIdWithProductAndUser(@Param("userId") String userId);
}
