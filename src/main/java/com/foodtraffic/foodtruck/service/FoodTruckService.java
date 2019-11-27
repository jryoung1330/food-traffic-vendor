package com.foodtraffic.foodtruck.service;

import java.util.List;

import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.model.dto.FoodTruckDto;

public interface FoodTruckService {

	List<FoodTruckDto> getAllFoodTrucks(String name, String city, String state, Integer zipCode);

	FoodTruckDto getFoodTruck(long id);

	FoodTruckDto createFoodTruck(FoodTruck foodTruck, String accessToken);

	FoodTruckDto updateFoodTruck(FoodTruck foodTruck);

	boolean checkFoodTruckExists(String foodTruckName, long id);
}
