package com.api.gestion.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FacturaService {
    ResponseEntity<String> generateReporte(Map<String, String> requestaMap);
}
