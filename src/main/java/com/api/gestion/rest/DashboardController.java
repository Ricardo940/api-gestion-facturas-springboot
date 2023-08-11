package com.api.gestion.rest;

import com.api.gestion.service.DashboardService;
import com.api.gestion.service.impl.DashboardServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/detalle")
    public ResponseEntity<Map<String, Object>> getCount(){
        return dashboardService.getCount();
    }
}
