package com.api.gestion.pojo;

import jakarta.persistence.*;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Producto.getAllProductos", query = "select new com.api.gestion.wrapper.ProductoWrapper(p.id, p.nombre, p.precio, p.descripcion, p.status, p.categoria.id, p.categoria.nombre) FROM Producto p")
@NamedQuery(name = "Producto.updateStatus", query = "UPDATE Producto p set p.status =: status where p.id =: id")
@NamedQuery(name = "Producto.getAllProductosByCategoria", query = "select new com.api.gestion.wrapper.ProductoWrapper(p.id, p.nombre, p.precio, p.descripcion, p.status, p.categoria.id, p.categoria.nombre) FROM Producto p WHERE p.categoria.id =: categoriaId and p.status='true'")
@Entity
@Table(name = "productos")
@Data
@DynamicUpdate
@DynamicInsert
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_fk", nullable = false)
    private Categoria categoria;

}
