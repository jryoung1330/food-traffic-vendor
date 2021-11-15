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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VendorServiceTest {

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

    public static final String ACCESS_TOKEN = "test";

    @BeforeEach
    public void setup() {
        vendor = mockVendor();
    }

    @Test
    public void givenId_whenGetVendor_thenReturnVendor() {
        when(vendorRepo.findById(1000L)).thenReturn(Optional.of(vendor));
        VendorDto result = vendorService.getVendor(1000L);
        assertNotNull(result);
    }

    @Test
    public void givenIdDoesNotExist_whenGetVendor_thenThrowException() {
        when(vendorRepo.findById(1000L)).thenReturn(Optional.empty());
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.getVendor(vendor.getId()));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenValidUserName_whenCheckVendorExists_thenReturnTrue() {
        when(vendorRepo.existsByUserName("mockfoodtruck")).thenReturn(true);
        assertTrue(vendorService.checkVendorExists(vendor.getUserName()));
    }

    @Test
    public void givenValidId_whenCheckVendorExists_thenReturnTrue() {
        when(vendorRepo.existsById(1000L)).thenReturn(true);
        assertTrue(vendorService.checkVendorExists(vendor.getId()));
    }

    @Test
    public void givenValidRequest_whenUpdateVendor_thenReturnUpdatedVendor() {
        UserDto user = mockUser();
        EmployeeDto employee = mockEmployee();
        employee.setEmployeeId(user.getId());
        employee.setVendorId(vendor.getId());
        user.setEmployee(employee);

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        when(vendorRepo.existsById(1000L)).thenReturn(true);
        when(vendorRepo.saveAndFlush(vendor)).thenReturn(updatedVendor);

        VendorDto result = vendorService.updateVendor(ACCESS_TOKEN, 1000L, updatedVendor);

        assertEquals("Changed", result.getDisplayName());
    }

    @Test
    public void givenInvalidId_whenUpdateVendor_thenThrowException() {
        UserDto user = mockUser();
        EmployeeDto employee = mockEmployee();
        employee.setEmployeeId(user.getId());
        employee.setVendorId(vendor.getId());
        user.setEmployee(employee);

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.updateVendor(ACCESS_TOKEN, 1001L, updatedVendor));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenVendorDoesNotExist_whenUpdateVendor_thenThrowException() {
        UserDto user = mockUser();
        EmployeeDto employee = mockEmployee();
        employee.setEmployeeId(user.getId());
        employee.setVendorId(vendor.getId());
        user.setEmployee(employee);

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        when(vendorRepo.existsById(1000L)).thenReturn(false);

        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.updateVendor(ACCESS_TOKEN, 1000L, updatedVendor));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenUserIsNotAnAdmin_whenUpdateVendor_thenThrowException() {
        UserDto user = mockUser();
        EmployeeDto employee = mockEmployee();
        employee.setEmployeeId(user.getId());
        employee.setVendorId(vendor.getId());
        employee.setAdmin(false);
        user.setEmployee(employee);

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        when(vendorRepo.existsById(1000L)).thenReturn(true);

        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.updateVendor(ACCESS_TOKEN, 1000L, updatedVendor));
        assertEquals(HttpStatus.UNAUTHORIZED, rse.getStatus());
    }

    @Test
    public void givenWrongOwner_whenUpdateVendor_thenThrowException() {
        UserDto user = mockUser();
        EmployeeDto employee = mockEmployee();
        employee.setEmployeeId(user.getId());
        employee.setVendorId(1001L);
        user.setEmployee(employee);

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        when(vendorRepo.existsById(1000L)).thenReturn(true);

        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.updateVendor(ACCESS_TOKEN, 1000L, updatedVendor));
        assertEquals(HttpStatus.UNAUTHORIZED, rse.getStatus());
    }

    @Test
    public void givenUserNotAnEmployee_whenUpdateVendor_thenThrowException() {
        UserDto user = mockUser();

        Vendor updatedVendor = mockVendor();
        updatedVendor.setDisplayName("Changed");

        when(userClient.checkAccessHeader(anyString())).thenReturn(user);
        when(vendorRepo.existsById(1000L)).thenReturn(true);

        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> vendorService.updateVendor(ACCESS_TOKEN, 1000L, updatedVendor));
        assertEquals(HttpStatus.UNAUTHORIZED, rse.getStatus());
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
        mockVendor.setId(1000L);
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
}
