package com.lococater.service;

import com.lococater.entity.FoodTruck;
import com.lococater.repository.FoodTruckRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodTruckService {

    FoodTruckRepo foodTruckRepo;

    @Autowired
    public FoodTruckService(FoodTruckRepo foodTruckRepo){
        this.foodTruckRepo = foodTruckRepo;
    }

    public List<FoodTruck> getAllFoodTrucks() {
        return foodTruckRepo.findAll();
    }

    public List<FoodTruck> getAllFoodTrucks(Integer zipCode) {
        return foodTruckRepo.findAllByZipCode(zipCode);
    }

    public List<FoodTruck> getAllFoodTrucks(String city, String state) {
        return foodTruckRepo.findAllByCityAndState(city, state);
    }

    public FoodTruck getFoodTruck(Long id) {
        Optional<FoodTruck> optionalFoodTruck = foodTruckRepo.findById(id);
        return optionalFoodTruck.isPresent() ? optionalFoodTruck.get() : null;
    }

    public List<FoodTruck> getAllFoodTrucks(String name) {
        return foodTruckRepo.findAllByNameIgnoreCaseContaining(name);
    }

    public FoodTruck createFoodTruck(FoodTruck foodTruck){
        String message;
        if((message = validateCreateRequest(foodTruck)) == null) {
            return foodTruckRepo.saveAndFlush(foodTruck);
        } else {
            throw new RuntimeException(message);
        }
    }

    public FoodTruck updateFoodTruck(FoodTruck foodTruck){
        String message;
        if((message = validateUpdateRequest(foodTruck)) == null) {
            return foodTruckRepo.saveAndFlush(foodTruck);
        } else {
            throw new RuntimeException(message);
        }
    }

    private String validateCreateRequest(FoodTruck foodTruck){
        // validate id
        if(foodTruck.getId() > 0) return "Invalid id";

        // validate address
        // TODO: use USPS address validation

        // get coordinates

        // check for duplicate food truck name
        if(foodTruckRepo.existsByNameAndCompany(foodTruck.getName(), foodTruck.getCompany()))
            return "Duplicate food truck name";

        return null;
    }

    private String validateUpdateRequest(FoodTruck foodTruck) {
        if (foodTruck.getId() <= 0) return "Invalid id";

        // validate address
        // TODO: use USPS address validation

        // get coordinates

        // check if food truck exists
        if (foodTruck.getName() == null || foodTruck.getCompany() == null ||
                !foodTruckRepo.existsByNameAndCompany(foodTruck.getName(), foodTruck.getCompany())) {
            return "Food Truck does not exists";
        }
        return null;
    }
}