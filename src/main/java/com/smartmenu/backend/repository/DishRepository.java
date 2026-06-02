package com.smartmenu.backend.repository;

import com.smartmenu.backend.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findByCategory(String category);
    List<Dish> findByVegetarian(Boolean vegetarian);
    List<Dish> findBySpicy(Boolean spicy);
    List<Dish> findByPriceLessThanEqual(Double maxPrice);
    Optional<Dish> findByNameIgnoreCase(String name);
    List<Dish> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}