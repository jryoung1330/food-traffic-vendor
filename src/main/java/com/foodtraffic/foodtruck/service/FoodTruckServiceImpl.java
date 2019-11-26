package com.foodtraffic.foodtruck.service;

import com.foodtraffic.client.EmployeeClient;
import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.entity.FoodTruckStatus;
import com.foodtraffic.foodtruck.repository.FoodTruckRepository;
import com.foodtraffic.model.dto.FoodTruckDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.model.request.EmployeeRequest;
import com.foodtraffic.model.response.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FoodTruckServiceImpl implements FoodTruckService {

    @Autowired
    FoodTruckRepository foodTruckRepo;

    @Autowired
    UserClient userClient;

    @Autowired
    EmployeeClient employeeClient;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<FoodTruckDto> getAllFoodTrucks(String name, String city, String state, Integer zipCode) {
        String params = validateGetRequest(name, city, state, zipCode);
        List<FoodTruck> foodTrucks;

        switch(params){
            case "name": foodTrucks = getAllFoodTrucks(name); break;
            case "citystate": foodTrucks = getAllFoodTrucks(city, state); break;
            case "zip code": foodTrucks = getAllFoodTrucks(zipCode); break;
            default: throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return modelMapper.map(foodTrucks, new TypeToken<List<FoodTruckDto>>(){}.getType());
    }

    @Override
    public FoodTruckDto getFoodTruck(long id) {
        Optional<FoodTruck> optionalFoodTruck = foodTruckRepo.findById(id);
        return optionalFoodTruck.isPresent() ? modelMapper.map(optionalFoodTruck.get(), FoodTruckDto.class) : null;
    }

    @Override
    public FoodTruckDto createFoodTruck(FoodTruck foodTruck, String accessToken){
        // check user access
        UserDto user;
        try {
            user = userClient.checkAccessHeader(accessToken);
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.valueOf(e.status()), ErrorResponse.extractErrorMessage(e.getMessage()));
        }

        // validate request
        if(validateCreateRequest(foodTruck)) {

            // create food truck
            foodTruck.setId(0L);
            foodTruck.setStatus(FoodTruckStatus.HOLD.getStatusNum());
            foodTruck = foodTruckRepo.saveAndFlush(foodTruck);

            createOwner(foodTruck.getId(), user.getId());

            return modelMapper.map(foodTruck, FoodTruckDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
    }

    @Override
    public FoodTruckDto updateFoodTruck(FoodTruck foodTruck){
        String message;
        if((message = validateUpdateRequest(foodTruck)) == null) {
            foodTruck = foodTruckRepo.saveAndFlush(foodTruck);
            return modelMapper.map(foodTruck, FoodTruckDto.class);
        } else {
            throw new RuntimeException(message);
        }
    }

    @Override
    public boolean checkFoodTruckExists(String foodTruckName, long id) {
        if (foodTruckName != null) {
            return foodTruckRepo.existsByFoodTruckName(foodTruckName);
        } else if (id > 0) {
            return foodTruckRepo.existsById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food Truck Name or Id is required");
        }
    }

    /*
     * helper methods
     */

    public List<FoodTruck> getAllFoodTrucks(String name) {
        return foodTruckRepo.findAllByDisplayNameIgnoreCaseContaining(name);
    }

    public List<FoodTruck> getAllFoodTrucks(Integer zipCode) {
        return foodTruckRepo.findAllByZipCode(zipCode);
    }

    public List<FoodTruck> getAllFoodTrucks(String city, String state) {
        return foodTruckRepo.findAllByCityAndState(city, state);
    }

    private boolean validateCreateRequest(FoodTruck foodTruck){
        try {
            return (foodTruck.getFoodTruckName().matches("[a-zA-Z0-9_]+")
                    && foodTruck.getFoodTruckName().length() >= 4
                    && foodTruck.getFoodTruckName().length() <= 25
                    && foodTruckRepo.existsByFoodTruckName(foodTruck.getFoodTruckName())
                    && foodTruck.getDisplayName().length() <= 100
                    && foodTruck.getCity() != null
                    && foodTruck.getState() != null
                    && foodTruck.getDescription().length() > 0
                    && foodTruck.getDescription().length() <= 300);
        } catch (NullPointerException e) {
            // catch null values, will return false here
            // but throw exception (Bad Request) later
        }
        return false;
    }

    private void createOwner(long foodTruckId, long userId) {
        EmployeeRequest employee = new EmployeeRequest();
        employee.setUserId(userId);
        employee.setOwner(true);
        employee.setAdmin(true);
        employee.setAssociate(true);

        try {
            employeeClient.createEmployee(foodTruckId, employee);
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.valueOf(e.status()), ErrorResponse.extractErrorMessage(e.getMessage()));
        }
    }

    private String validateUpdateRequest(FoodTruck foodTruck) {
        if (foodTruck.getId() <= 0) return "Invalid id";

        // validate address
        // TODO: use USPS address validation

        // get coordinates

        // check if food truck exists

        return null;
    }

    private String validateGetRequest(String name, String city, String state, Integer zipCode) {
        String request = "";
        if(name != null) request += "name";
        if(city != null) request += "city";
        if(state != null) request += "state";
        if(zipCode != null) request += "zip code";
        return request;
    }
}
