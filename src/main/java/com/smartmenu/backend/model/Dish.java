package com.smartmenu.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "dishes")
public class Dish {
    @Id
    private String id;
    private String name;
    private String category;
    private Double price;
    private String currency;
    private Boolean available;
    private Integer stock;
    private String description;
    private List<String> allergens;
    private Boolean spicy;
    private Boolean vegetarian;
    private Integer prepTime;
}