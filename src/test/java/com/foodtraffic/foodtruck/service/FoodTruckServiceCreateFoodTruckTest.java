package com.foodtraffic.foodtruck.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import com.foodtraffic.client.EmployeeClient;
import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.repository.FoodTruckRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.FoodTruckDto;
import com.foodtraffic.model.dto.UserDto;

@SpringBootTest
public class FoodTruckServiceCreateFoodTruckTest {

    @Mock
    FoodTruckRepository foodTruckRepo;

    @Mock
    UserClient userClient;

    @Mock
    EmployeeClient employeeClient;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    FoodTruckServiceImpl foodTruckService;

    FoodTruck foodTruck;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        foodTruck = new FoodTruck();
        foodTruck.setId((long) 0);
        foodTruck.setFoodTruckName("mockfoodtruck");
        foodTruck.setDisplayName("Mock Food Truck");
        foodTruck.setCompany("Test LLC");
        foodTruck.setDescription("Test test test");
        foodTruck.setZipCode(100);
        foodTruck.setCity("Narnia");
        foodTruck.setState("KT");
        foodTruck.setLongitude(0.0);
        foodTruck.setLatitude(0.0);
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(foodTruckRepo.existsByFoodTruckName(anyString())).thenReturn(true);
        when(foodTruckRepo.saveAndFlush(anyObject())).thenReturn(mockFoodTruck());
        when(employeeClient.createEmployee(anyObject(), anyObject())).thenReturn(mockEmployee());
    }

    @Test
    public void givenValidRequest_whenCreateFoodTruck_returnFoodTruck() {
        FoodTruckDto foodTruck = foodTruckService.createFoodTruck(mockFoodTruck(), "test");
        assertNotNull(foodTruck);
    }

    @Test
    public void givenNullFoodTruckName_whenCreateFoodTruck_throwException() {
        foodTruck.setFoodTruckName(null);
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createFoodTruck(foodTruck, "test"));
    }

    @Test
    public void givenInvalidFoodTruckName_whenCreateFoodTruck_throwException() {
        foodTruck.setFoodTruckName("mock foodtruck");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createFoodTruck(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckLengthLT4_whenCreateFoodTruck_throwException() {
        foodTruck.setFoodTruckName("moc");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createFoodTruck(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckLengthGT25_whenCreateFoodTruck_throwException() {
        foodTruck.setFoodTruckName("thisnameislongerthantwentyfivecharacters");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createFoodTruck(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckNameAlreadyExists_whenCreateFoodTruck_throwException() {
        when(foodTruckRepo.existsByFoodTruckName(anyString())).thenReturn(false);
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createFoodTruck(foodTruck, "test"));
    }

    private UserDto mockUser() {
        UserDto user = new UserDto();
        user.setId(0L);
        user.setUsername("test");
        user.setEmail("test@test.com");
        return user;
    }

    private EmployeeDto mockEmployee() {
        EmployeeDto employee = new EmployeeDto();
        employee.setOwner(true);
        employee.setAdmin(true);
        employee.setAssociate(true);
        return employee;
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
        mockFoodTruck.setLongitude(0.0);
        mockFoodTruck.setLatitude(0.0);
        return mockFoodTruck;
    }
}
