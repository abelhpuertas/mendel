package com.mendel.model;

import com.mendel.model.entity.TransactionEntity;
import com.mendel.model.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void shouldSaveAndFindTransaction_whenValidTransactionProvided() {
        // Given
        Long id = 1L;
        TransactionEntity tx = TransactionEntity.builder().id(id).amount(new BigDecimal("100.0")).type("cars").build();

        // When
        repository.save(id, tx);
        TransactionEntity result = repository.findById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(0, new BigDecimal("100.0").compareTo(result.getAmount()));
        assertEquals("cars", result.getType());
    }

    @Test
    void shouldReturnIds_whenTransactionsByTypeExist() {
        // Given
        repository.save(1L, TransactionEntity.builder().id(1L).type("cars").build());
        repository.save(2L, TransactionEntity.builder().id(2L).type("shopping").build());
        repository.save(3L, TransactionEntity.builder().id(3L).type("cars").build());

        // When
        List<Long> result = repository.findIdsByType("cars");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(3L));
    }

    @Test
    void shouldReturnChildrenIds_whenParentIdProvided() {
        // Given
        repository.save(1L, TransactionEntity.builder().id(1L).build());
        repository.save(2L, TransactionEntity.builder().id(2L).parentId(1L).build());
        repository.save(3L, TransactionEntity.builder().id(3L).parentId(1L).build());
        repository.save(4L, TransactionEntity.builder().id(4L).parentId(2L).build());

        // When
        List<Long> result = repository.findChildren(1L);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
        assertFalse(result.contains(4L));
    }

    @Test
    void shouldReturnNull_whenFindingNonExistentTransaction() {
        // When
        TransactionEntity result = repository.findById(999L);

        // Then
        assertNull(result);
    }
}
