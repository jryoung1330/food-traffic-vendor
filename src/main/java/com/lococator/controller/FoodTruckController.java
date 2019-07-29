package com.lococator.controller;

import com.lococator.entity.FoodTruck;
import com.lococator.service.FoodTruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/foodtrucks")
public class FoodTruckController {

    FoodTruckService foodTruckService;

    @Autowired
    public FoodTruckController(FoodTruckService foodTruckService) {
        this.foodTruckService = foodTruckService;
    }

    @GetMapping("/")
    public List<FoodTruck> getFoodTrucks() {
        return foodTruckService.getAllFoodTrucks();
    }

    @GetMapping("")
    public List<FoodTruck> getFoodTrucks(@RequestParam(required = false) Integer zipcode,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) String name) {

        if(zipcode != null) return foodTruckService.getAllFoodTrucks(zipcode);
        if(city != null && state != null) return foodTruckService.getAllFoodTrucks(city, state);
        if(name != null) return foodTruckService.getAllFoodTrucks(name);
        return null;
    }

    @GetMapping("/{id}")
    public FoodTruck getFoodTruck(@PathVariable Long id) {
        return foodTruckService.getFoodTruck(id);
    }

    @PostMapping("")
    public FoodTruck createFoodTruck(@RequestBody FoodTruck foodTruck) {
        return foodTruckService.createFoodTruck(foodTruck);
    }

    @PutMapping("/{id}")
    public FoodTruck updateFoodTruck(@RequestBody FoodTruck foodTruck) {
        return foodTruckService.updateFoodTruck(foodTruck);
    }
}
