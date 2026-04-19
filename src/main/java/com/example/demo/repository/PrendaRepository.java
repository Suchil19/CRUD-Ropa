package com.example.demo.repository;

import com.example.demo.model.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, Long> {
    // Aquí heredamos todos los métodos CRUD (save, delete, find)
}