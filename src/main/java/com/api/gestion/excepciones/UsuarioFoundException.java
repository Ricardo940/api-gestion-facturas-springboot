package com.api.gestion.excepciones;

public class UsuarioFoundException extends Exception{


    public UsuarioFoundException(){
        super("Usuario con ese email ya existe, vuelva a intentarlo !!");
    }

     public UsuarioFoundException(String mensaje){
        super(mensaje);
    }
}
