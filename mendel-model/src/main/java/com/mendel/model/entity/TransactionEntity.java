package com.mendel.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    private Long id;
    private BigDecimal amount;
    private String type;
    private Long parentId;
}
