package com.mendel.business;

import com.mendel.dto.api.v1.TransactionDto;
import com.mendel.business.exception.MendelException;
import com.mendel.business.service.TransactionService;
import com.mendel.model.entity.TransactionEntity;
import com.mendel.model.repository.InMemoryTransactionRepository;
import com.mendel.util.enums.EMendelExceptionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private InMemoryTransactionRepository repository;

    @InjectMocks
    private TransactionService transactionService;

    // --- saveTransaction Tests ---

    @Test
    void shouldThrowMendelException_whenTransactionParentIdEqualsItsId() {
        Long id = 1L;
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("50.0"))
                .type("gift")
                .parentId(id)
                .build();

        MendelException exception = assertThrows(MendelException.class, () -> transactionService.saveTransaction(id, dto));
        assertEquals(EMendelExceptionCode.INVALID_TRANSACTION_DATA, exception.getExceptionCode());
        verify(repository, never()).save(any(), any());
    }

    @Test
    void shouldThrowMendelException_whenParentTransactionNotFound() {
        Long id = 1L;
        Long parentId = 2L;
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("50.0"))
                .type("gift")
                .parentId(parentId)
                .build();
        
        when(repository.findById(parentId)).thenReturn(null);

        MendelException exception = assertThrows(MendelException.class, () -> transactionService.saveTransaction(id, dto));
        assertEquals(EMendelExceptionCode.TRANSACTION_NOT_FOUND, exception.getExceptionCode());
        verify(repository).findById(parentId);
        verify(repository, never()).save(any(), any());
    }

    @Test
    void shouldSaveTransaction_whenValidDataProvided() {
        Long id = 1L;
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("50.0"))
                .type("gift")
                .build();

        transactionService.saveTransaction(id, dto);

        verify(repository).save(eq(id), any(TransactionEntity.class));
    }

    @Test
    void shouldSaveTransaction_whenValidDataProvidedWithParent() {
        Long id = 1L;
        Long parentId = 2L;
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("50.0"))
                .type("gift")
                .parentId(parentId)
                .build();
        
        TransactionEntity parentEntity = TransactionEntity.builder().amount(new BigDecimal("10.0")).type("cars").build();
        when(repository.findById(parentId)).thenReturn(parentEntity);

        transactionService.saveTransaction(id, dto);

        verify(repository).findById(parentId);
        verify(repository).save(eq(id), any(TransactionEntity.class));
    }

    // --- getTransactionIdsByType Tests ---

    @Test
    void shouldReturnEmptyList_whenNoTransactionsByTypeFound() {
        String type = "unknown";
        when(repository.findIdsByType(type)).thenReturn(List.of());

        List<Long> result = transactionService.getTransactionIdsByType(type);

        assertTrue(result.isEmpty());
        verify(repository).findIdsByType(type);
    }

    @Test
    void shouldReturnTransactionIds_whenTransactionsByTypeExist() {
        String type = "cars";
        List<Long> expectedIds = List.of(1L, 2L, 3L);
        when(repository.findIdsByType(type)).thenReturn(expectedIds);

        List<Long> result = transactionService.getTransactionIdsByType(type);

        assertEquals(expectedIds.size(), result.size());
        assertTrue(result.containsAll(expectedIds));
        verify(repository).findIdsByType(type);
    }

    // --- getSum Tests ---

    @Test
    void shouldThrowMendelException_whenGetSumNonExistentIdProvided() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(null);

        MendelException exception = assertThrows(MendelException.class, () -> transactionService.getSum(id));
        assertEquals(EMendelExceptionCode.TRANSACTION_NOT_FOUND, exception.getExceptionCode());
        verify(repository).findById(id);
    }

    @Test
    void shouldReturnSum_whenSingleTransactionExists() {
        Long id = 1L;
        BigDecimal amount = new BigDecimal("100.0");
        TransactionEntity entity = TransactionEntity.builder().amount(amount).type("cars").build();
        when(repository.findById(id)).thenReturn(entity);
        when(repository.findChildren(id)).thenReturn(List.of());

        BigDecimal result = transactionService.getSum(id);

        assertEquals(0, amount.compareTo(result));
        verify(repository, times(2)).findById(id);
        verify(repository).findChildren(id);
    }

    @Test
    void shouldReturnTransitiveSum_whenMultipleLevelsExist() {
        Long id1 = 1L;
        Long id2 = 2L;
        Long id3 = 3L;

        TransactionEntity tx1 = TransactionEntity.builder().amount(new BigDecimal("10.0")).build();
        TransactionEntity tx2 = TransactionEntity.builder().amount(new BigDecimal("20.0")).parentId(id1).build();
        TransactionEntity tx3 = TransactionEntity.builder().amount(new BigDecimal("30.0")).parentId(id2).build();

        when(repository.findById(id1)).thenReturn(tx1);
        when(repository.findById(id2)).thenReturn(tx2);
        when(repository.findById(id3)).thenReturn(tx3);

        when(repository.findChildren(id1)).thenReturn(List.of(id2));
        when(repository.findChildren(id2)).thenReturn(List.of(id3));
        when(repository.findChildren(id3)).thenReturn(List.of());

        BigDecimal result = transactionService.getSum(id1);

        assertEquals(0, new BigDecimal("60.0").compareTo(result));
        verify(repository, times(2)).findById(id1);
        verify(repository).findById(id2);
        verify(repository).findById(id3);
    }

    @Test
    void shouldReturnSumWithoutInfiniteLoop_whenCycleDetectedInRepository() {
        Long id1 = 1L;
        Long id2 = 2L;

        TransactionEntity tx1 = TransactionEntity.builder().amount(new BigDecimal("10.0")).build();
        TransactionEntity tx2 = TransactionEntity.builder().amount(new BigDecimal("20.0")).parentId(id1).build();

        when(repository.findById(id1)).thenReturn(tx1);
        when(repository.findById(id2)).thenReturn(tx2);

        when(repository.findChildren(id1)).thenReturn(List.of(id2));
        when(repository.findChildren(id2)).thenReturn(List.of(id1));

        BigDecimal result = transactionService.getSum(id1);

        assertEquals(0, new BigDecimal("30.0").compareTo(result));
    }

    @Test
    void shouldReturnSum_whenChildTransactionHasNullAmount() {
        Long id1 = 1L;
        Long id2 = 2L;

        TransactionEntity tx1 = TransactionEntity.builder().amount(new BigDecimal("10.0")).build();
        TransactionEntity tx2 = TransactionEntity.builder().amount(null).parentId(id1).build();

        when(repository.findById(id1)).thenReturn(tx1);
        when(repository.findById(id2)).thenReturn(tx2);

        when(repository.findChildren(id1)).thenReturn(List.of(id2));
        when(repository.findChildren(id2)).thenReturn(List.of());

        BigDecimal result = transactionService.getSum(id1);

        assertEquals(0, new BigDecimal("10.0").compareTo(result));
        verify(repository, times(2)).findById(id1);
        verify(repository).findById(id2);
    }
}