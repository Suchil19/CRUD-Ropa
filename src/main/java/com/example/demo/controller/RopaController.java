package com.example.demo.controller;

import com.example.demo.model.Prenda;
import com.example.demo.repository.PrendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RopaController {

    @Autowired
    private PrendaRepository repository;

    /**
     * MÉTODO PRIVADO: Recalcula las estadísticas de inventario.
     * Se usa para mantener los números actualizados en todas las vistas.
     */
    private void cargarEstadisticas(Model model, List<Prenda> lista) {
        // Suma de todas las unidades físicas (stock)
        int totalUnidades = lista.stream()
                .mapToInt(p -> p.getCantidad() != null ? p.getCantidad() : 0)
                .sum();
        
        // Suma del valor monetario total (Precio * Cantidad)
        double valorTotal = lista.stream()
                .mapToDouble(p -> (p.getPrecio() != null ? p.getPrecio() : 0.0) * (p.getCantidad() != null ? p.getCantidad() : 0))
                .sum();

        model.addAttribute("totalUnidades", totalUnidades);
        model.addAttribute("valorTotal", valorTotal);
        model.addAttribute("totalModelos", lista.size());
    }

    // --- RUTA: PÁGINA DE BIENVENIDA (INDEX) ---
    @GetMapping("/")
    public String home(Model model) {
        List<Prenda> lista = repository.findAll();
        cargarEstadisticas(model, lista);
        return "index"; // Carga index.html
    }

    // --- RUTA: LISTAR INVENTARIO Y FORMULARIO ---
    @GetMapping("/ropa")
    public String inventario(Model model) {
        List<Prenda> lista = repository.findAll();
        model.addAttribute("prendas", lista);
        model.addAttribute("prenda", new Prenda());
        // Objeto vacío para el formulario
        cargarEstadisticas(model, lista);
        return "inventario"; // Carga inventario.html
    }

    // --- RUTA: GUARDAR O ACTUALIZAR ---
    @PostMapping("/ropa/guardar")
    public String guardar(@ModelAttribute Prenda prenda) {
        repository.save(prenda);
        return "redirect:/ropa"; // Refresca la lista y los totales
    }

    // --- RUTA: CARGAR DATOS PARA EDITAR ---
    @GetMapping("/ropa/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Prenda prenda = repository.findById(id).orElse(new Prenda());
        List<Prenda> lista = repository.findAll();
        
        model.addAttribute("prendas", lista);
        model.addAttribute("prenda", prenda); // Carga el objeto con datos en el formulario
        cargarEstadisticas(model, lista);
        
        return "inventario";
    }

    // --- RUTA: ELIMINAR PRENDA ---
    @GetMapping("/ropa/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/ropa";
    }
}