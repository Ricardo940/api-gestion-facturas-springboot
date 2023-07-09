package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;


@NamedQuery(name = "User.findByEmail", query = "select u from User u where u.email =: email ")
@NamedQuery(name = "User.getAllUsers", query = "select new com.api.gestion.wrapper.UserWrapper(u.id, u.nombre, u.email, u.numeroContacto, u.status) from User u where u.role = 'user'")
@NamedQuery(name = "User.getAllAdmins", query = "select u.email from User u where u.role = 'admin'")
@NamedQuery(name = "User.updateStatus", query = "update User u set u.status =: status where u.id =: id ")
@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    @Column(name = "numeroDeContacto")
    private String numeroContacto;
    private String email;
    private String password;
    private String status;
    private String role;
}
