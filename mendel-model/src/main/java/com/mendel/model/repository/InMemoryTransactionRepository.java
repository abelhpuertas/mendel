package com.mendel.model.repository;

import com.mendel.model.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionRepository {
    
    private final Map<Long, TransactionEntity> storage = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> typeIndex = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> childrenIndex = new ConcurrentHashMap<>();

    public void save(Long id, TransactionEntity transaction) {
        TransactionEntity oldTransaction = storage.put(id, transaction);
        
        if (Objects.nonNull(oldTransaction)) {
            removeFromIndex(typeIndex, oldTransaction.getType(), id);
            if (Objects.nonNull(oldTransaction.getParentId())) {
                removeFromIndex(childrenIndex, oldTransaction.getParentId(), id);
            }
        }
        
        addToIndex(typeIndex, transaction.getType(), id);
        if (Objects.nonNull(transaction.getParentId())) {
            addToIndex(childrenIndex, transaction.getParentId(), id);
        }
    }

    public TransactionEntity findById(Long id) {
        return storage.get(id);
    }

    public List<Long> findIdsByType(String type) {
        Set<Long> ids = typeIndex.get(type);
        return Objects.nonNull(ids) ? new ArrayList<>(ids) : Collections.emptyList();
    }

    public List<Long> findChildren(Long parentId) {
        Set<Long> ids = childrenIndex.get(parentId);
        return Objects.nonNull(ids) ? new ArrayList<>(ids) : Collections.emptyList();
    }

    private <K> void addToIndex(Map<K, Set<Long>> index, K key, Long id) {
        if (Objects.nonNull(key)) {
            index.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(id);
        }
    }

    private <K> void removeFromIndex(Map<K, Set<Long>> index, K key, Long id) {
        if (Objects.nonNull(key)) {
            Set<Long> ids = index.get(key);
            if (Objects.nonNull(ids)) {
                ids.remove(id);
            }
        }
    }
}
