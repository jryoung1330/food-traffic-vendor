package com.foodtraffic.foodtruck.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.Employee;
import com.foodtraffic.foodtruck.repository.EmployeeRepo;
import com.foodtraffic.foodtruck.repository.FoodTruckRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    FoodTruckRepo foodTruckRepo;

    @Autowired
    UserClient userClient;

    public Employee createEmployee(Long id, Employee employee) {
        if(foodTruckRepo.existsById(id)
                // && userClient.checkUserExists(null, employee.getUserId())
                && !employeeRepo.existsByUserId(employee.getUserId())) {
            employee.setId(0L);
            employee.setStatus(2); // TODO: use employee status enum
            employee.setFoodTruckId(id);
            return employeeRepo.saveAndFlush(employee);
        } else {
            // TODO
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
