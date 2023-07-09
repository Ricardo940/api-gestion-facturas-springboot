package com.api.gestion.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoWrapper {

    private Integer id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String status;
    private Integer categoriaId;
    private String nombreCategoria;
}
