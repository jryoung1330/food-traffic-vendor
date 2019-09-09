package com.lococater.controller;

import com.lococater.entity.FoodTruck;
import com.lococater.service.FoodTruckService;
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

    @GetMapping("")
    public List<FoodTruck> getFoodTrucks(@RequestParam(required = false, name = "zip-code") Integer zipCode,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) String name) {

        if(zipCode != null) return foodTruckService.getAllFoodTrucks(zipCode);
        if(city != null && state != null) return foodTruckService.getAllFoodTrucks(city, state);
        if(name != null) return foodTruckService.getAllFoodTrucks(name);
        return foodTruckService.getAllFoodTrucks();
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
