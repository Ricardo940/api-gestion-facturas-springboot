package com.api.gestion.dao;

import com.api.gestion.pojo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaDAO extends JpaRepository<Factura, Integer> {
}
