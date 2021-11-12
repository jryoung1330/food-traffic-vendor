package com.foodtraffic.vendor.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.repository.VendorRepository;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateVendorTest {

    @Mock
    VendorRepository vendorRepo;

    @Mock
    UserClient userClient;

    @Mock
    EmployeeService employeeService;

    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    VendorServiceImpl vendorService;

    Vendor vendor;

    @BeforeEach
    public void setup() {
        vendor = mockVendor();
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
    }

    @Test
    public void givenValidRequest_whenCreateVendor_thenReturnVendor() {
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        when(vendorRepo.saveAndFlush(any())).thenReturn(mockVendor());
        when(employeeService.createEmployee(anyLong(), any())).thenReturn(mockEmployee());
        VendorDto result = vendorService.createVendor(vendor, "test");
        assertNotNull(result);
    }

    @Test
    public void givenNullName_whenCreateVendor_thenThrowException() {
        vendor.setUserName(null);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenInvalidName_whenCreateVendor_thenThrowException() {
        vendor.setUserName("mock vendor");
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNameLengthLT4_whenCreateVendor_thenThrowException() {
        vendor.setUserName("moc");
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNameLengthGT25_whenCreateVendor_thenThrowException() {
        vendor.setUserName("thisnameislongerthantwentyfivecharacters");
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNameAlreadyExists_whenCreateVendor_thenThrowException() {
        when(vendorRepo.existsByUserName(anyString())).thenReturn(true);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenDisplayNameEmpty_whenCreateVendor_thenThrowException() {
        vendor.setDisplayName("");
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenDisplayNameGT50_whenCreateVendor_thenThrowException() {
        vendor.setDisplayName("thisnameislongerthanfiftycharactersthisnameislongerthanfiftycharacters");
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNullState_whenCreateVendor_thenThrowException() {
        vendor.setState(null);
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNullCity_whenCreateVendor_thenThrowException() {
        vendor.setCity(null);
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenDescriptionLongerThan300Chars_whenCreateVendor_thenThrowException() {
        vendor.setDescription(getStringLongerThan(300));
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.createVendor(vendor, "test"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }
    
    @Test
    public void givenNullDescription_whenCreateVendor_thenCreateVendor() {
        vendor.setDescription(null);
        when(vendorRepo.existsByUserName(anyString())).thenReturn(false);
        when(vendorRepo.saveAndFlush(any())).thenReturn(mockVendor());
        when(employeeService.createEmployee(anyLong(), any())).thenReturn(mockEmployee());
        vendorService.createVendor(vendor, "test");
        verify(vendorRepo, times(1)).saveAndFlush(any());
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

    private Vendor mockVendor() {
        Vendor mockVendor = new Vendor();
        mockVendor.setId((long) 0);
        mockVendor.setUserName("mockfoodtruck");
        mockVendor.setDisplayName("Mock Food Truck");
        mockVendor.setCompany("Test LLC");
        mockVendor.setDescription("Test test test");
        mockVendor.setZipCode(100);
        mockVendor.setCity("Narnia");
        mockVendor.setState("KT");
        mockVendor.setLongitude(0.0);
        mockVendor.setLatitude(0.0);
        mockVendor.setOwner(100L);
        return mockVendor;
    }

    private String getStringLongerThan(int threshold) {
        String blah = "blah";
        int bound = (threshold / blah.length()) + 1;
        return blah.repeat(Math.max(0, bound));
    }
}
