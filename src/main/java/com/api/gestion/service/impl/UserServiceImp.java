package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDAO;
import com.api.gestion.mapper.UserMapperImp;
import com.api.gestion.pojo.User;
import com.api.gestion.security.CustomerDetailsService;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.security.jwt.JwtUtil;
import com.api.gestion.service.UserService;
import com.api.gestion.util.EmailUtils;
import com.api.gestion.util.FacturaUtils;
import com.api.gestion.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import java.util.*;

@Slf4j
@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private UserMapperImp userMapperImp;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ResponseEntity<?> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario");
        try{
            if(validateSignUpMap(requestMap)){
                User user = userDAO.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    User userRequest = getUserFromMap(requestMap);
                    userRequest.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
                    userDAO.save(userRequest);
                    return FacturaUtils.getResponseEntity("Usuario registrado con exito", HttpStatus.OK);
                }else {
                    return FacturaUtils.getResponseEntity("El usuario con ese email ya existe", HttpStatus.BAD_REQUEST);
                }
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> login(Map<String, String> requestMap) {
        log.info("Dentro de login");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if(authentication.isAuthenticated()){
                if(customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),
                                    customerDetailsService.getUserDetail().getRole())+"\"}",
                            HttpStatus.OK);
                }else {
                    return new ResponseEntity<>("{\"mensaje\":"+"\"status is false\"}", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception e){
            log.error("{}", e);
        }
        return new ResponseEntity<>("{\"mensaje\":"+"\"credenciales incorrectas\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDAO.getAllUsers(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<User> optionalUser = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(optionalUser.isPresent()){
                    userDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    enviarCorreoToAdmins(requestMap.get("status"),optionalUser.get().getEmail(), userDAO.getAllAdmins());
                    return FacturaUtils.getResponseEntity("status del usuario actualizado", HttpStatus.OK);
                }else {
                    return FacturaUtils.getResponseEntity("Este usuario no existe",HttpStatus.NOT_FOUND);
                }
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return FacturaUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserWrapper> getUserActual() {
        

        if(jwtFilter.getCurrentUser()!=null){
            User user = userDAO.findByEmail(jwtFilter.getCurrentUser());
        
            
            return new ResponseEntity<>(userMapperImp.mapearDeUser(user), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(jwtFilter.getCurrentUser());

            if(user != null){
                if(user.getPassword().equals(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userDAO.save(user);
                    return FacturaUtils.getResponseEntity("Contraseña actualizada con exito", HttpStatus.OK);
                }else {
                    return FacturaUtils.getResponseEntity("Contraseña incorrecta", HttpStatus.BAD_REQUEST);
                }
            }else {
                return FacturaUtils.getResponseEntity("Este usuario no existe",HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {

            if(!Strings.isNullOrEmpty(requestMap.get("email"))){
                User user = userDAO.findByEmail(requestMap.get("email"));
                emailUtils.forgotPassword(user.getEmail(), "Credenciales del sistema gestion de facturas", user.getPassword());
                return FacturaUtils.getResponseEntity("Revisa tu email", HttpStatus.OK);
            }else {
                return FacturaUtils.getResponseEntity("Este usuario no existe",HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void enviarCorreoToAdmins(String status, String user, List<String> allAdmins){
        allAdmins.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Cuenta aprobada", "USUARIO : "+ user + "\n es aprobado por \nADMIN : "+ jwtFilter.getCurrentUser(), allAdmins);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Cuenta desaprobada", "USUARIO : "+ user + "\n es desaprobado por \nADMIN : "+ jwtFilter.getCurrentUser(), allAdmins);

        }
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if(requestMap.containsKey("nombre") && requestMap.containsKey("numeroContacto")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }

        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setNombre(requestMap.get("nombre"));
        user.setNumeroContacto(requestMap.get("numeroContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return  user;
    }
}
