package com.api.gestion.service.impl;

import com.api.gestion.dao.CategoriaDAO;
import com.api.gestion.dao.FacturaDAO;
import com.api.gestion.dao.ProductoDAO;
import com.api.gestion.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImp implements DashboardService {

    @Autowired
    private ProductoDAO productoDAO;
    @Autowired
    private CategoriaDAO categoriaDAO;
    @Autowired
    private FacturaDAO facturaDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("Producto", productoDAO.count());
        map.put("Categorias", categoriaDAO.count());
        map.put("Facturas", facturaDAO.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
