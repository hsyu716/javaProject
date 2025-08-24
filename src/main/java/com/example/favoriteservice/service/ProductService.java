package com.example.favoriteservice.service;

import com.example.favoriteservice.entity.Product;
import com.example.favoriteservice.repository.ProductRepository;
import com.example.favoriteservice.repository.LikeListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LikeListRepository likeListRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setProductName(productDetails.getProductName());
            product.setPrice(productDetails.getPrice());
            product.setFeeRate(productDetails.getFeeRate());
            return productRepository.save(product);
        }
        return null;
    }

    public void deleteProduct(Long id) {
        likeListRepository.deleteByProductNo(id);
        productRepository.deleteById(id);
    }
}
