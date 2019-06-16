package com.lococator.controller;

import com.lococator.entity.FoodTruck;
import com.lococator.service.FoodTruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foodtrucks")
public class FoodTruckController {

    FoodTruckService foodTruckService;

    @Autowired
    public FoodTruckController(FoodTruckService foodTruckService) {
        this.foodTruckService = foodTruckService;
    }

    @GetMapping("")
    public List<FoodTruck> getFoodTrucks() {
        return foodTruckService.getAllFoodTrucks();
    }

    @GetMapping("/zipcode={zipCode}")
    public List<FoodTruck> getFoodTrucks(@PathVariable Integer zipCode) {
        return foodTruckService.getAllFoodTrucks(zipCode);
    }

    @GetMapping("/city={city}/state={state}")
    public List<FoodTruck> getFoodTrucks(@PathVariable String city, @PathVariable String state) {
        return foodTruckService.getAllFoodTrucks(city, state);
    }

    @GetMapping("/id={id}")
    public FoodTruck getFoodTruck(@PathVariable Long id) {
        return foodTruckService.getFoodTruck(id);
    }

    @GetMapping("/name={name}")
    public FoodTruck getFoodTruck(@PathVariable String name) {
        return foodTruckService.getFoodTruck(name);
    }

    @PostMapping("")
    public FoodTruck createFoodTruck(@RequestBody FoodTruck foodTruck) {
        return foodTruckService.createFoodTruck(foodTruck);
    }

    @PutMapping("/id={id}")
    public FoodTruck updateFoodTruck(@RequestBody FoodTruck foodTruck) {
        return foodTruckService.updateFoodTruck(foodTruck);
    }
}
