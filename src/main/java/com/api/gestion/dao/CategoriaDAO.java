package com.api.gestion.dao;

import com.api.gestion.pojo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {

    @Query("SELECT c FROM Categoria c")
    List<Categoria> getAllCategory();
}
