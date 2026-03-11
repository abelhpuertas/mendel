package com.mendel.api.controller;

import com.mendel.dto.api.v1.StatusResponseDto;
import com.mendel.dto.api.v1.SumResponseDto;
import com.mendel.dto.api.v1.TransactionDto;
import com.mendel.business.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponseDto> putTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto) {
        transactionService.saveTransaction(id, transactionDto);
        return ResponseEntity.ok(StatusResponseDto.builder().status("ok").build());
    }

    @GetMapping("/types/{type}")
    public ResponseEntity<List<Long>> getTransactionsByType(@PathVariable String type) {
        List<Long> ids = transactionService.getTransactionIdsByType(type);
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/sum/{id}")
    public ResponseEntity<SumResponseDto> getSum(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getSum(id));
    }
}
