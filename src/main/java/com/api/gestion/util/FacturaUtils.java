package com.api.gestion.util;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FacturaUtils {

    private FacturaUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus httpStatus){
        return new ResponseEntity<>("{\"Mensaje\" : \""+ message+"\"}", httpStatus);
    }

    public static String getUuid(){
        Date date = new Date();
        long time = date.getTime();
        return "FACTURA" + time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data)){
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){
            }.getType());
        }

        return new HashMap<>();
    }
}
