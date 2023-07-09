package com.api.gestion.dao;

import com.api.gestion.pojo.Producto;
import com.api.gestion.wrapper.ProductoWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ProductoDAO extends JpaRepository<Producto, Integer> {

    List<ProductoWrapper> getAllProductos();

    @Transactional
    @Modifying
    Integer updateStatus(@Param("id") Integer id, @Param("status") String status);

    List<ProductoWrapper> getAllProductosByCategoria(@Param("categoriaId") Integer id);

    @Query(value = "SELECT new com.api.gestion.wrapper.ProductoWrapper(p.id, p.nombre, p.precio, p.descripcion, p.status, p.categoria.id, p.categoria.nombre) FROM Producto p WHERE p.id=:id")
    ProductoWrapper productById(@Param("id") Integer id);
}
