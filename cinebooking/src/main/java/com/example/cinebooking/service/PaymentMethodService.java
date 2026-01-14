package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Payment.PaymentMethodDTO;
import com.example.cinebooking.repository.PaymentMethodRepository;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repo;
    public PaymentMethodService(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    public List<PaymentMethodDTO> getActiveMethods() {
        return repo.findByIsActiveTrueOrderBySortOrderAsc()
            .stream()
            .map(m -> {
            PaymentMethodDTO dto = new PaymentMethodDTO();
            dto.setCode(m.getCode());
            dto.setName(m.getName());
            return dto;
        })
        .collect(Collectors.toList());
    }
    
}
