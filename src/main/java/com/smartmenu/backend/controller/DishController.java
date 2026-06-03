package com.smartmenu.backend.controller;

import com.smartmenu.backend.model.Dish;
import com.smartmenu.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DishController {

    private final DishRepository dishRepository;

    // GET /api/dishes - get all dishes with optional filters
    @GetMapping
    public List<Dish> getAllDishes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String vegetarian,
            @RequestParam(required = false) String spicy,
            @RequestParam(required = false) Double maxPrice) {

        List<Dish> dishes = dishRepository.findAll();

        if (category != null)
            dishes = dishes.stream().filter(d -> category.equalsIgnoreCase(d.getCategory())).collect(Collectors.toList());
        if (vegetarian != null)
            dishes = dishes.stream().filter(d -> d.getVegetarian() != null && d.getVegetarian().equals(Boolean.parseBoolean(vegetarian))).collect(Collectors.toList());
        if (spicy != null)
            dishes = dishes.stream().filter(d -> d.getSpicy() != null && d.getSpicy().equals(Boolean.parseBoolean(spicy))).collect(Collectors.toList());
        if (maxPrice != null)
            dishes = dishes.stream().filter(d -> d.getPrice() <= maxPrice).collect(Collectors.toList());

        return dishes;
    }

    // GET /api/dishes/search
    @GetMapping("/search")
    public List<Dish> searchDishes(
            @RequestParam(required = false) String excludeAllergen,
            @RequestParam(required = false) String keyword) {

        List<Dish> dishes = dishRepository.findAll();

        if (excludeAllergen != null) {
            String allergen = excludeAllergen.toLowerCase();
            dishes = dishes.stream()
                    .filter(d -> d.getAllergens() == null ||
                            d.getAllergens().stream().noneMatch(a -> a.toLowerCase().contains(allergen)))
                    .collect(Collectors.toList());
        }
        if (keyword != null) {
            String kw = keyword.toLowerCase();
            dishes = dishes.stream()
                    .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(kw)) ||
                            (d.getDescription() != null && d.getDescription().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        return dishes;
    }

    // PATCH /api/dishes/{name}/price
    @PatchMapping("/{name}/price")
    public ResponseEntity<?> updatePrice(@PathVariable String name, @RequestBody Map<String, Double> body) {
        return dishRepository.findByNameIgnoreCase(name).map(dish -> {
            dish.setPrice(body.get("price"));
            dishRepository.save(dish);
            return ResponseEntity.ok(dish);
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/dishes/{name}/stock
    @PatchMapping("/{name}/stock")
    public ResponseEntity<?> updateStock(@PathVariable String name, @RequestBody Map<String, Integer> body) {
        return dishRepository.findByNameIgnoreCase(name).map(dish -> {
            dish.setStock(body.get("stock"));
            dishRepository.save(dish);
            return ResponseEntity.ok(dish);
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/dishes/{name}/availability
    @PatchMapping("/{name}/availability")
    public ResponseEntity<?> updateAvailability(@PathVariable String name, @RequestBody Map<String, Boolean> body) {
        return dishRepository.findByNameIgnoreCase(name).map(dish -> {
            dish.setAvailable(body.get("available"));
            dishRepository.save(dish);
            return ResponseEntity.ok(dish);
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET /api/dishes/summary - 返回简洁格式给 Agent
    @GetMapping("/summary")
    public List<Map<String, Object>> getDishSummary(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String vegetarian,
            @RequestParam(required = false) String spicy,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String excludeAllergen) {

        List<Dish> dishes = dishRepository.findAll();

        if (category != null)
            dishes = dishes.stream().filter(d -> category.equalsIgnoreCase(d.getCategory())).collect(Collectors.toList());
        if (vegetarian != null)
            dishes = dishes.stream().filter(d -> d.getVegetarian() != null && d.getVegetarian().equals(Boolean.parseBoolean(vegetarian))).collect(Collectors.toList());
        if (spicy != null)
            dishes = dishes.stream().filter(d -> d.getSpicy() != null && d.getSpicy().equals(Boolean.parseBoolean(spicy))).collect(Collectors.toList());
        if (maxPrice != null)
            dishes = dishes.stream().filter(d -> d.getPrice() <= maxPrice).collect(Collectors.toList());
        if (excludeAllergen != null) {
            String allergen = excludeAllergen.toLowerCase();
            dishes = dishes.stream()
                    .filter(d -> d.getAllergens() == null ||
                            d.getAllergens().stream().noneMatch(a -> a.toLowerCase().contains(allergen)))
                    .collect(Collectors.toList());
        }

        return dishes.stream().map(d -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("name", d.getName());
            m.put("price", "$" + d.getPrice());
            m.put("category", d.getCategory());
            m.put("vegetarian", d.getVegetarian());
            m.put("spicy", d.getSpicy());
            m.put("allergens", d.getAllergens());
            m.put("available", d.getAvailable());
            m.put("description", d.getDescription());
            return m;
        }).collect(Collectors.toList());
    }
}