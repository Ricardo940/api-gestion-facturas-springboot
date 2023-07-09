package com.api.gestion.excepciones;

public class UsuarioNotFoundExcepcion extends Exception {
    
    public UsuarioNotFoundExcepcion(String mensaje){
        super(mensaje);
    }
}
