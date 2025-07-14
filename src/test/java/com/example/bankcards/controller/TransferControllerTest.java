package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.dto.TransferResponseDTO;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnSuccessResponse_WhenValidRequest() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L,
                2L,
                30d);
        LocalDateTime now = LocalDateTime.now();
        TransferResponseDTO response = new TransferResponseDTO(1L,
                2L,
                10d, now,
                "Success");

        when(transferService.transfer(any(TransferRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCardId").value(1L))
                .andExpect(jsonPath("$.toCardId").value(2L))
                .andExpect(jsonPath("$.amount").value(10d))
                .andExpect(jsonPath("$.status").value("Success"));

        verify(transferService, times(1)).transfer(any(TransferRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        String invalidRequest = """
            {
                "fromCardId": 0,
                "toCardId": 0,
                "amount": -100
            }
            """;

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(transferService, never()).transfer(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnBadRequest_WhenInsufficientFunds() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, 30d);
        String errorMessage = "Недостаточно средств для перевода";

        when(transferService.transfer(any(TransferRequestDTO.class)))
                .thenThrow(new InsufficientFundsException("Недостаточно средств для перевода"));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));

        verify(transferService, times(1)).transfer(any(TransferRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnBadRequest_WhenCardNotFound() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, 30d);

        when(transferService.transfer(any(TransferRequestDTO.class)))
                .thenThrow(new CardNotFoundException(1L));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, 30d);

        when(transferService.transfer(any(TransferRequestDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));


        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}