package com.mendel.api.controller;

import com.mendel.dto.api.v1.StatusResponseDto;
import com.mendel.dto.api.v1.SumResponseDto;
import com.mendel.dto.api.v1.TransactionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnOk_whenPuttingTransaction() {
        TransactionDto dto = TransactionDto.builder()
                .amount(new BigDecimal("5000.0"))
                .type("cars")
                .build();

        ResponseEntity<StatusResponseDto> response = restTemplate.exchange(
                "/transactions/10",
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                StatusResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getStatus());
    }

    @Test
    public void shouldReturnTransactionIds_whenGettingByType() {
        TransactionDto dto1 = TransactionDto.builder().amount(new BigDecimal("5000.0")).type("test_type_cars").build();
        TransactionDto dto2 = TransactionDto.builder().amount(new BigDecimal("10000.0")).type("test_type_shopping").build();
        TransactionDto dto3 = TransactionDto.builder().amount(new BigDecimal("5000.0")).type("test_type_cars").build();

        restTemplate.exchange("/transactions/20", HttpMethod.PUT, new HttpEntity<>(dto1), StatusResponseDto.class);
        restTemplate.exchange("/transactions/21", HttpMethod.PUT, new HttpEntity<>(dto2), StatusResponseDto.class);
        restTemplate.exchange("/transactions/22", HttpMethod.PUT, new HttpEntity<>(dto3), StatusResponseDto.class);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "/transactions/types/test_type_cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Long>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(20L));
        assertTrue(response.getBody().contains(22L));
    }

    @Test
    public void shouldReturnSum_whenGettingSum() {
        TransactionDto dto1 = TransactionDto.builder().amount(new BigDecimal("5000.0")).type("cars").build();
        TransactionDto dto2 = TransactionDto.builder().amount(new BigDecimal("10000.0")).type("shopping").parentId(30L).build();
        TransactionDto dto3 = TransactionDto.builder().amount(new BigDecimal("5000.0")).type("shopping").parentId(31L).build();

        restTemplate.exchange("/transactions/30", HttpMethod.PUT, new HttpEntity<>(dto1), StatusResponseDto.class);
        restTemplate.exchange("/transactions/31", HttpMethod.PUT, new HttpEntity<>(dto2), StatusResponseDto.class);
        restTemplate.exchange("/transactions/32", HttpMethod.PUT, new HttpEntity<>(dto3), StatusResponseDto.class);

        ResponseEntity<SumResponseDto> response = restTemplate.exchange(
                "/transactions/sum/30",
                HttpMethod.GET,
                null,
                SumResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // 5000 + 10000 + 5000 = 20000
        assertEquals(0, new BigDecimal("20000.0").compareTo(response.getBody().getSum()));
    }
}