package com.foodtraffic.foodtruck.service;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyObject;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.foodtruck.entity.FoodTruck;
import com.foodtraffic.foodtruck.repository.FoodTruckRepo;
import com.foodtraffic.foodtruck.service.EmployeeService;
import com.foodtraffic.foodtruck.service.FoodTruckServiceImpl;
import com.foodtraffic.model.dto.FoodTruckDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FoodTruckServiceGetListTest {

    @Mock
    FoodTruckRepo foodTruckRepo;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    FoodTruckServiceImpl foodTruckService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(foodTruckRepo.findAllByZipCode(anyInt())).thenReturn(mockFoodTruckList());
        when(foodTruckRepo.findAllByCityAndState(anyString(), anyString())).thenReturn(mockFoodTruckList());
        when(foodTruckRepo.findAllByDisplayNameIgnoreCaseContaining(anyString())).thenReturn(mockFoodTruckList());
    }

    @Test
    public void givenZipCode100_getAllFoodTrucks_returnsAListFoodTruckWithZipCode100() {
       List<FoodTruckDto> result = foodTruckService.getAllFoodTrucks(null, null, null, 100);
       assertEquals(100, result.get(0).getZipCode());
    }

    @Test
    public void givenCityNarniaAndStateKT_getAllFoodTrucks_returnsAListFoodTruckWithCityNarniaAndStateKT() {
        List<FoodTruckDto> result = foodTruckService.getAllFoodTrucks(null, "Narnia", "KT", null);
        assertTrue("Narnia".equals(result.get(0).getCity()) && "KT".equals(result.get(0).getState()));
    }

    @Test
    public void givenName_getAllFoodTrucks_returnsAListOfFoodTruckWithGivenName() {
        List<FoodTruckDto> result = foodTruckService.getAllFoodTrucks("Mock Food Truck", null, null, null);
        assertEquals("Mock Food Truck", result.get(0).getName());
    }

    @Test
    public void givenCityButNoState_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, "Narnia", null, null));
    }

    @Test
    public void givenStateButNoCity_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, null, "KT", null));
    }

    @Test
    public void givenNoParameters_getAllFoodTrucks_returnAllFoodTrucks() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, null, null, null));
    }

    @Test
    public void givenZipCodeAndState_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, null, "KT", 100));
    }

    @Test
    public void givenZipCodeAndCity_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, "Narnia", null, 100));
    }

    @Test
    public void givenZipCodeAndName_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks("Mock Food Truck", null, null, 100));
    }

    @Test
    public void givenCityAndStateAndZipCode_getAllFoodTrucks_throwsException() {
        assertThrows(ResponseStatusException.class, () -> foodTruckService.getAllFoodTrucks(null, "Narnia", "KT", 100));
    }

    private List<FoodTruck> mockFoodTruckList() {
        List<FoodTruck> mockList = new ArrayList<>();
        mockList.add(mockFoodTruck());
        return mockList;
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
