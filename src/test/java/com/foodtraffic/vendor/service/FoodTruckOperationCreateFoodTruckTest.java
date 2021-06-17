package com.foodtraffic.vendor.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.foodtraffic.model.dto.VendorDto;
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
import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.repository.VendorRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.UserDto;

@SpringBootTest
public class FoodTruckOperationCreateFoodTruckTest {

    @Mock
    VendorRepository vendorRepo;

    @Mock
    UserClient userClient;

    @Mock
    EmployeeClient employeeClient;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    VendorServiceImpl foodTruckService;

    Vendor foodTruck;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        foodTruck = new Vendor();
        foodTruck.setId((long) 0);
        foodTruck.setUserName("mockfoodtruck");
        foodTruck.setDisplayName("Mock Food Truck");
        foodTruck.setCompany("Test LLC");
        foodTruck.setDescription("Test test test");
        foodTruck.setZipCode(100);
        foodTruck.setCity("Narnia");
        foodTruck.setState("KT");
        foodTruck.setLongitude(0.0);
        foodTruck.setLatitude(0.0);
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(vendorRepo.existsByUserName(anyString())).thenReturn(true);
        when(vendorRepo.saveAndFlush(anyObject())).thenReturn(mockFoodTruck());
        when(employeeClient.createEmployee(anyObject(), anyObject())).thenReturn(mockEmployee());
    }

    @Test
    public void givenValidRequest_whenCreateFoodTruck_returnFoodTruck() {
        VendorDto foodTruck = foodTruckService.createVendor(mockFoodTruck(), "test");
        assertNotNull(foodTruck);
    }

    @Test
    public void givenNullFoodTruckName_whenCreateFoodTruck_throwException() {
        foodTruck.setUserName(null);
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createVendor(foodTruck, "test"));
    }

    @Test
    public void givenInvalidFoodTruckName_whenCreateFoodTruck_throwException() {
        foodTruck.setUserName("mock vendor");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createVendor(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckLengthLT4_whenCreateFoodTruck_throwException() {
        foodTruck.setUserName("moc");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createVendor(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckLengthGT25_whenCreateFoodTruck_throwException() {
        foodTruck.setUserName("thisnameislongerthantwentyfivecharacters");
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createVendor(foodTruck, "test"));
    }

    @Test
    public void givenFoodTruckNameAlreadyExists_whenCreateFoodTruck_throwException() {
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        assertThrows(ResponseStatusException.class, ()->foodTruckService.createVendor(foodTruck, "test"));
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
        employee.setAdmin(true);
        employee.setAssociate(true);
        return employee;
    }

    private Vendor mockFoodTruck() {
        Vendor mockFoodTruck = new Vendor();
        mockFoodTruck.setId((long) 0);
        mockFoodTruck.setUserName("mockfoodtruck");
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
