package com.api.gestion.service;

import com.api.gestion.pojo.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface CategoriaService {

    ResponseEntity<String> addNuevaCategoria(Map<String,String> requestMap);
    ResponseEntity<String> updateCategoria(Map<String,String> requestMap);
    ResponseEntity<String> deleteCategoria(Integer id);
    ResponseEntity<List<Categoria>> getAllCategory(String valueFilter);
}
