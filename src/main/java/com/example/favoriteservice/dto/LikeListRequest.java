package com.example.favoriteservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeListRequest {
    
    @NotBlank(message = "使用者ID不能為空")
    private String userId;
    
    @NotNull(message = "產品編號不能為空")
    private Long productNo;
    
    @NotNull(message = "購買數量不能為空")
    @Min(value = 1, message = "購買數量必須大於0")
    private Integer quantity;
    
    @NotBlank(message = "扣款帳號不能為空")
    private String account;
    
    private String orderName;
}
