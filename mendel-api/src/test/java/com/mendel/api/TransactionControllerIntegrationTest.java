package com.mendel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendel.dto.api.v1.TransactionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOkStatus_whenTransactionIsSaved() throws Exception {
        // Given
        Long id = 1L;
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("10.5"))
                .type("cars")
                .build();

        // When & Then
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void shouldReturnTransactionIdsByType_whenTransactionsExist() throws Exception {
        // Given
        TransactionDto dto = TransactionDto.builder().amount(new BigDecimal("100.0")).type("cars").build();
        mockMvc.perform(put("/transactions/10").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)));
        mockMvc.perform(put("/transactions/20").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)));

        // When & Then
        mockMvc.perform(get("/transactions/types/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnSum_whenParentAndChildrenTransactionsExist() throws Exception {
        // Given
        // Parent: 1 (100.0) -> Child: 2 (50.0)
        TransactionDto parentDto = TransactionDto.builder().amount(new BigDecimal("100.0")).type("cars").build();
        TransactionDto childDto = TransactionDto.builder().amount(new BigDecimal("50.0")).type("cars").parentId(1L).build();

        mockMvc.perform(put("/transactions/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(parentDto)));
        mockMvc.perform(put("/transactions/2").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(childDto)));

        // When & Then
        mockMvc.perform(get("/transactions/sum/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(150.0));
    }

    @Test
    void shouldReturnNotFound_whenSumRequestedForNonExistentId() throws Exception {
        // When & Then
        mockMvc.perform(get("/transactions/sum/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENDEL-001"));
    }
}
