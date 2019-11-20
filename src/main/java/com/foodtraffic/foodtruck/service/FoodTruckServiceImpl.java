package com.foodtraffic.foodtruck.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.Employee;
import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.entity.FoodTruckStatus;
import com.foodtraffic.foodtruck.repository.FoodTruckRepo;
import com.foodtraffic.model.dto.FoodTruckDto;
import com.foodtraffic.model.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FoodTruckServiceImpl implements FoodTruckService {

    @Autowired
    FoodTruckRepo foodTruckRepo;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    UserClient userClient;

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

    public List<FoodTruck> getAllFoodTrucks(String name) {
        return foodTruckRepo.findAllByDisplayNameIgnoreCaseContaining(name);
    }

    public List<FoodTruck> getAllFoodTrucks(Integer zipCode) {
        return foodTruckRepo.findAllByZipCode(zipCode);
    }

    public List<FoodTruck> getAllFoodTrucks(String city, String state) {
        return foodTruckRepo.findAllByCityAndState(city, state);
    }

    @Override
    public FoodTruckDto getFoodTruck(Long id) {
        Optional<FoodTruck> optionalFoodTruck = foodTruckRepo.findById(id);
        return optionalFoodTruck.isPresent() ? modelMapper.map(optionalFoodTruck.get(), FoodTruckDto.class) : null;
    }

    @Override
    public FoodTruckDto createFoodTruck(FoodTruck foodTruck, String accessToken){
        // check user access
        UserDto user = userClient.checkAccessHeader(accessToken);

        // validate request
        if(validateCreateRequest(foodTruck)) {

            // create food truck
            foodTruck.setId(0L);
            foodTruck.setStatus(FoodTruckStatus.HOLD.getStatusNum());
            foodTruck = foodTruckRepo.saveAndFlush(foodTruck);

            // TODO: make request to the employee service
            // make requesting user the owner
            Employee employee = new Employee();
            employee.setUserId(user.getId());
            employee.setOwner(true);
            employee.setAdmin(true);
            employee.setAssociate(true);

            employeeService.createEmployee(foodTruck.getId(), employee);

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

    private boolean validateCreateRequest(FoodTruck foodTruck){
        return (foodTruck.getFoodTruckName().length() <= 25
                && foodTruck.getDisplayName().length() <= 100
                && foodTruck.getCompany().length() <= 100
                && foodTruck.getCity() != null
                && foodTruck.getState() != null
                && foodTruck.getDescription() != null);
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
