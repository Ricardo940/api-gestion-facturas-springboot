package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.CategoriaDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.CategoriaService;
import com.api.gestion.util.FacturaUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoriaServiceImp implements CategoriaService {

    @Autowired
    CategoriaDAO categoriaDAO;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevaCategoria(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoriaMap(requestMap, false)){
                    categoriaDAO.save(getCategoriaFromMap(requestMap, false));
                    return FacturaUtils.getResponseEntity("Categoria agregada con exito", HttpStatus.OK);
                }
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategoria(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoriaMap(requestMap, true)){
                    Optional<Categoria> categoria = categoriaDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(categoria.isPresent()){
                        categoriaDAO.save(getCategoriaFromMap(requestMap, true));
                        return FacturaUtils.getResponseEntity("Categoria actualizada con exito", HttpStatus.OK);
                    }
                    return FacturaUtils.getResponseEntity("La categoria con ese Id no existe", HttpStatus.BAD_REQUEST);
                }
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteCategoria(Integer id) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<Categoria> categoria = categoriaDAO.findById(id);
                if(categoria.isPresent()){
                    categoriaDAO.deleteById(id);
                    return FacturaUtils.getResponseEntity("Categoria eliminada con exito", HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity("La categoria con ese Id no existe", HttpStatus.BAD_REQUEST);
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Categoria>> getAllCategory(String valueFilter) {
        try {
            if(!Strings.isNullOrEmpty(valueFilter) && valueFilter.equalsIgnoreCase("true")){
                return new ResponseEntity<>(categoriaDAO.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoriaDAO.findAll(),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateCategoriaMap(Map<String, String> requestMap, boolean validateId){
        if(requestMap.containsKey("nombre")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }
            if(!validateId){
                return true;
            }
        }
        return false;

    }

    private Categoria getCategoriaFromMap(Map<String, String> requestMap, boolean isAdd){
        Categoria categoria = new Categoria();
        if(isAdd){
            categoria.setId(Integer.parseInt(requestMap.get("id")));
        }
        categoria.setNombre(requestMap.get("nombre"));
        return categoria;
    }
}

