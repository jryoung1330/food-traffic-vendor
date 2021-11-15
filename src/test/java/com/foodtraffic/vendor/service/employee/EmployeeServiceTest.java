package com.foodtraffic.vendor.service.employee;

import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.vendor.entity.employee.Employee;
import com.foodtraffic.vendor.repository.employee.EmployeeRepository;
import com.foodtraffic.vendor.service.VendorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepo;

    @Mock
    private VendorService vendorService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private static final String ACCESS_TOKEN = "test";

    @Test
    public void givenAssociate_whenGetEmployeeByVendor_thenReturnEmployees() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(true);
        when(employeeRepo.findAllByVendorId(1000L)).thenReturn(List.of(mockAssociate(), mockAdmin()));
        List<EmployeeDto> result = employeeService.getEmployeeByVendor(1000L, "all");
        assertEquals(2, result.size());
    }

    @Test
    public void givenAdmin_whenGetEmployeeByVendor_thenReturnAdmin() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(true);
        when(employeeRepo.findAllByVendorIdAndIsAdmin(1000L, true)).thenReturn(List.of(mockAdmin()));
        List<EmployeeDto> result = employeeService.getEmployeeByVendor(1000L, "admin");
        assertEquals(1, result.size());
    }

    @Test
    public void givenInvalidVendor_whenGetEmployeeByVendor_thenThrowException() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> employeeService.getEmployeeByVendor(1000L, "admin"));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenValidEmployee_whenCreateEmployee_thenReturnEmployee() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(true);
        when(employeeRepo.existsById(101L)).thenReturn(false);
        when(employeeRepo.saveAndFlush(any())).thenReturn(mockAssociate());
        EmployeeDto employee = employeeService.createEmployee(1000L, mockAssociate());
        assertNotNull(employee);
    }

    @Test
    public void givenInvalidVendor_whenCreateEmployee_thenThrowException() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> employeeService.createEmployee(1000L, mockAssociate()));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenEmployeeAlreadyExists_whenCreateEmployee_thenThrowException() {
        when(vendorService.checkVendorExists(1000L)).thenReturn(true);
        when(employeeRepo.existsById(101L)).thenReturn(true);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, ()-> employeeService.createEmployee(1000L, mockAssociate()));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    private Employee mockAssociate() {
        Employee employee = new Employee();
        employee.setAssociate(true);
        employee.setAdmin(false);
        employee.setVendorId(1000L);
        employee.setId(101L);
        return employee;
    }

    private Employee mockAdmin() {
        Employee employee = new Employee();
        employee.setAssociate(true);
        employee.setAdmin(true);
        employee.setVendorId(1000L);
        employee.setId(100L);
        return employee;
    }
}
