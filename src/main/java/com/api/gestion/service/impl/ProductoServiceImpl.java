package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.ProductoDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.pojo.Producto;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.ProductoService;
import com.api.gestion.util.FacturaUtils;
import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoDAO productoDAO;
    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevoProducto(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductoMap(requestMap, false)){
                    productoDAO.save(getProductoFromMap(requestMap,false));
                    return FacturaUtils.getResponseEntity("Producto agregado con exito", HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoWrapper>> getAllProducts() {
        try{
            return new ResponseEntity<>(productoDAO.getAllProductos(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProducto(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductoMap(requestMap, true)){
                    Optional<Producto> productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(productoOptional.isPresent()){
                        Producto producto = getProductoFromMap(requestMap, true);
                        producto.setStatus(productoOptional.get().getStatus());
                        productoDAO.save(producto);
                        return FacturaUtils.getResponseEntity("Producto actualizado con exito", HttpStatus.OK);
                    }else {
                        return FacturaUtils.getResponseEntity("Producto no encontrado", HttpStatus.NOT_FOUND);
                    }
                }
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProducto(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Producto> productoOptional = productoDAO.findById(id);
                if(productoOptional.isPresent()){
                    productoDAO.deleteById(id);
                    return FacturaUtils.getResponseEntity("Producto eliminado con exito", HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity("Producto no encontrado", HttpStatus.NOT_FOUND);
            }else {
                return FacturaUtils.getResponseEntity("No tiene autorización", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Producto> productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(productoOptional.isPresent()){
                    productoDAO.updateStatus(productoOptional.get().getId(),requestMap.get("status"));
                    return FacturaUtils.getResponseEntity("Status del producto eliminado con exito", HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity("Producto no encontrado", HttpStatus.NOT_FOUND);
            }else {
                return FacturaUtils.getResponseEntity("No tiene autorización", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoWrapper>> getAllProductsByCategoria(Integer id) {
        try {
            return new ResponseEntity<>(productoDAO.getAllProductosByCategoria(id), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoWrapper> getProduct(Integer id) {
        try {
            ProductoWrapper productoWrapper = productoDAO.productById(id);
            if(productoWrapper != null){
                return new ResponseEntity<>(productoWrapper, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Producto getProductoFromMap(Map<String, String> requestMap, boolean isAdd) {
        Categoria categoria = new Categoria();
        categoria.setId(Integer.parseInt(requestMap.get("categoriaId")));
        Producto producto = new Producto();

        if(isAdd){
            producto.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            producto.setStatus("true");
        }

        producto.setNombre(requestMap.get("nombre"));
        producto.setPrecio(Double.parseDouble(requestMap.get("precio")));
        producto.setDescripcion(requestMap.get("descripcion"));
        producto.setCategoria(categoria);

        return producto;
    }

    private boolean validateProductoMap(Map<String, String> requestMap, boolean validateId){
        if(requestMap.containsKey("nombre") && requestMap.containsKey("descripcion")
                && requestMap.containsKey("precio") && requestMap.containsKey("categoriaId")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }
            if(!validateId){
                return true;
            }
        }
        return false;
    }
}
