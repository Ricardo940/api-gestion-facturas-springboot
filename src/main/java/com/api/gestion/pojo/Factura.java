package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Factura.getFacturas", query = "SELECT f FROM Factura f order by f.id desc")
@NamedQuery(name = "Factura.getFacturasByUsername", query = "SELECT f FROM Factura f where f.createBy=:username order by f.id desc")
@Entity
@Table(name = "facturas")
@Data
@DynamicUpdate
@DynamicInsert
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uuid;
    private String nombre;
    private String email;
    private String numeroContacto;
    private String metodoPago;
    private Double total;
    @Column(columnDefinition = "json")
    private String productoDetalles;
    private String createBy;
}
