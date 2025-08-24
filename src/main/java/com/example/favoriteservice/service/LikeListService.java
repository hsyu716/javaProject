package com.example.favoriteservice.service;

import com.example.favoriteservice.dto.LikeListRequest;
import com.example.favoriteservice.entity.LikeList;
import com.example.favoriteservice.entity.Product;
import com.example.favoriteservice.entity.User;
import com.example.favoriteservice.repository.LikeListRepository;
import com.example.favoriteservice.repository.ProductRepository;
import com.example.favoriteservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LikeListService {

    @Autowired
    private LikeListRepository likeListRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LikeList> getAllLikeLists() {
        return likeListRepository.findAll();
    }

    public LikeList getLikeListById(Long id) {
        return likeListRepository.findById(id).orElse(null);
    }

    public List<LikeList> getLikeListsByUserId(String userId) {
        return likeListRepository.findByUserId(userId);
    }

    public LikeList createLikeList(LikeListRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        Product product = productRepository.findById(request.getProductNo()).orElse(null);
        
        if (user == null || product == null) {
            return null;
        }

        LikeList likeList = new LikeList();
        likeList.setUser(user);
        likeList.setProduct(product);
        likeList.setQuantity(request.getQuantity());
        likeList.setAccount(request.getAccount());
        likeList.setOrderName(request.getOrderName());
        likeList.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal totalFee = totalAmount.multiply(product.getFeeRate());
        
        likeList.setTotalAmount(totalAmount);
        likeList.setTotalFee(totalFee);

        return likeListRepository.save(likeList);
    }

    public LikeList updateLikeList(Long id, LikeListRequest request) {
        LikeList likeList = likeListRepository.findById(id).orElse(null);
        if (likeList == null) {
            return null;
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        Product product = productRepository.findById(request.getProductNo()).orElse(null);
        if (user == null || product == null) {
            return null;
        }

        likeList.setUser(user);
        likeList.setProduct(product);
        likeList.setQuantity(request.getQuantity());
        likeList.setAccount(request.getAccount());
        likeList.setOrderName(request.getOrderName());

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal totalFee = totalAmount.multiply(product.getFeeRate());
        
        likeList.setTotalAmount(totalAmount);
        likeList.setTotalFee(totalFee);

        return likeListRepository.save(likeList);
    }

    public void deleteLikeList(Long id) {
        likeListRepository.deleteById(id);
    }
}
