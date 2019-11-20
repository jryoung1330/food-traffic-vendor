package com.foodtraffic.foodtruck.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.repository.FoodTruckRepo;
import com.foodtraffic.model.dto.FoodTruckDto;
import com.foodtraffic.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FoodTruckServiceCreateFoodTruckTest {

    @Mock
    FoodTruckRepo foodTruckRepo;

    @Mock
    UserClient userClient;

    @Mock
    EmployeeService employeeService;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    FoodTruckServiceImpl foodTruckService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(foodTruckRepo.saveAndFlush(anyObject())).thenReturn(mockFoodTruck());
    }

    @Test
    public void givenValidRequest_whenCreateFoodTruck_returnFoodTruck() {
        FoodTruckDto foodTruck = foodTruckService.createFoodTruck(mockFoodTruck(), "test");
        assertNotNull(foodTruck);
    }

    private UserDto mockUser() {
        UserDto user = new UserDto();
        user.setId(0L);
        user.setUsername("test");
        user.setEmail("test@test.com");
        return user;
    }

    private FoodTruck mockFoodTruck() {
        FoodTruck mockFoodTruck = new FoodTruck();
        mockFoodTruck.setId((long) 0);
        mockFoodTruck.setFoodTruckName("mockfoodtruck");
        mockFoodTruck.setDisplayName("Mock Food Truck");
        mockFoodTruck.setCompany("Test LLC");
        mockFoodTruck.setDescription("Test test test");
        mockFoodTruck.setZipCode(100);
        mockFoodTruck.setCity("Narnia");
        mockFoodTruck.setState("KT");
        mockFoodTruck.setStreetAddress("123 Main St");
        mockFoodTruck.setLongitude(0.0);
        mockFoodTruck.setLatitude(0.0);
        mockFoodTruck.setLocationDetails("Test test test");
        return mockFoodTruck;
    }
}
