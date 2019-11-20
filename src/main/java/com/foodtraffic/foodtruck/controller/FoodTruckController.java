package com.foodtraffic.foodtruck.controller;

import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.service.FoodTruckService;

import com.foodtraffic.model.dto.FoodTruckDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@Api(tags = "Food Truck", description = " ")
@RequestMapping("/foodtrucks")
public class FoodTruckController {

    @Autowired
    FoodTruckService foodTruckService;

    @GetMapping("")
    public List<FoodTruckDto> getFoodTrucks(@RequestParam(required = false, name = "zip-code") Integer zipCode,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) String name) {
        return foodTruckService.getAllFoodTrucks(name, city, state, zipCode);
    }

    @GetMapping("/{id}")
    public FoodTruckDto getFoodTruck(@PathVariable Long id) {
        return foodTruckService.getFoodTruck(id);
    }

    @PostMapping("")
    public FoodTruckDto createFoodTruck(@RequestBody FoodTruck foodTruck, @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return foodTruckService.createFoodTruck(foodTruck, accessToken);
    }

    @PutMapping("/{id}")
    public FoodTruckDto updateFoodTruck(@RequestBody FoodTruck foodTruck) {
        return foodTruckService.updateFoodTruck(foodTruck);
    }
}