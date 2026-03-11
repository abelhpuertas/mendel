package com.mendel.dto.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private BigDecimal amount;
    private String type;
    
    @JsonProperty("parent_id")
    private Long parentId;
}
