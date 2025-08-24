package com.example.favoriteservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @Column(name = "user_id", length = 20)
    private String userId;
    
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, length = 20)
    private String account;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
