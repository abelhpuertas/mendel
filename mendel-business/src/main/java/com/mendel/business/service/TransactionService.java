package com.mendel.business.service;

import com.mendel.model.entity.TransactionEntity;
import com.mendel.dto.api.v1.SumResponseDto;
import com.mendel.dto.api.v1.TransactionDto;
import com.mendel.business.exception.MendelException;
import com.mendel.util.enums.EMendelExceptionCode;
import com.mendel.business.mapper.TransactionMapper;
import com.mendel.model.repository.InMemoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final InMemoryTransactionRepository repository;

    public void saveTransaction(Long id, TransactionDto transactionDto) {
        log.debug("Saving transaction with id: {}", id);
        
        if (Objects.equals(id, transactionDto.getParentId())) {
            log.error("Transaction with id {} cannot be its own parent", id);
            throw new MendelException(EMendelExceptionCode.INVALID_TRANSACTION_DATA);
        }

        if (Objects.nonNull(transactionDto.getParentId()) && Objects.isNull(repository.findById(transactionDto.getParentId()))) {
            log.error("Parent transaction with id {} not found", transactionDto.getParentId());
            throw new MendelException(EMendelExceptionCode.TRANSACTION_NOT_FOUND);
        }

        TransactionEntity transaction = TransactionMapper.INSTANCE.toEntity(transactionDto);
        transaction.setId(id);
        repository.save(id, transaction);
    }

    public List<Long> getTransactionIdsByType(String type) {
        log.debug("Fetching transaction ids by type: {}", type);
        return repository.findIdsByType(type);
    }

    public SumResponseDto getSum(Long id) {
        log.debug("Calculating transitive sum for transaction id: {}", id);
        
        if (Objects.isNull(repository.findById(id))) {
            log.warn("Transaction with id {} not found for sum calculation", id);
            throw new MendelException(EMendelExceptionCode.TRANSACTION_NOT_FOUND);
        }

        BigDecimal totalSum = BigDecimal.ZERO;
        List<Long> allRelatedIds = new ArrayList<>();
        // Tracks visited transaction IDs to prevent infinite loops in case of circular dependencies
        Set<Long> visited = new HashSet<>();
        
        allRelatedIds.add(id);
        visited.add(id);

        for (int i = 0; i < allRelatedIds.size(); i++) {
            Long currentId = allRelatedIds.get(i);
            TransactionEntity currentTx = repository.findById(currentId);

            if (Objects.nonNull(currentTx)) {
                if (Objects.nonNull(currentTx.getAmount())) {
                    totalSum = totalSum.add(currentTx.getAmount());
                }
                
                for (Long childId : repository.findChildren(currentId)) {
                    if (visited.add(childId)) {
                        allRelatedIds.add(childId);
                    }
                }
            }
        }

        return SumResponseDto.builder().sum(totalSum).build();
    }
}
