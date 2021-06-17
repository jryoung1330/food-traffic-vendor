package com.foodtraffic.vendor.service;

import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FoodTruckOperationGetListTest {

    @Mock
    VendorRepository vendorRepo;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    VendorServiceImpl foodTruckService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(vendorRepo.findAllByZipCode(anyInt())).thenReturn(mockFoodTruckList());
        when(vendorRepo.findAllByCityAndState(anyString(), anyString())).thenReturn(mockFoodTruckList());
        when(vendorRepo.findAllByDisplayNameIgnoreCaseContaining(anyString())).thenReturn(mockFoodTruckList());
    }

    private List<Vendor> mockFoodTruckList() {
        List<Vendor> mockList = new ArrayList<>();
        mockList.add(mockFoodTruck());
        return mockList;
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
        mockFoodTruck.setStreetAddress("123 Main St");
        mockFoodTruck.setLongitude(0.0);
        mockFoodTruck.setLatitude(0.0);
        mockFoodTruck.setLocationDetails("Test test test");
        return mockFoodTruck;
    }

}
