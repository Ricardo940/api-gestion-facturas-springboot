package com.api.gestion.service;

import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductoService {

    ResponseEntity<String> addNuevoProducto(Map<String, String> requestMap);
    ResponseEntity<List<ProductoWrapper>> getAllProducts();
    ResponseEntity<String> updateProducto(Map<String, String> requestMap);
    ResponseEntity<String> deleteProducto(Integer id);
    ResponseEntity<String> updateStatus(Map<String, String> requestMap);
    ResponseEntity<List<ProductoWrapper>> getAllProductsByCategoria(Integer id);
    ResponseEntity<ProductoWrapper> getProduct(Integer id);

}
