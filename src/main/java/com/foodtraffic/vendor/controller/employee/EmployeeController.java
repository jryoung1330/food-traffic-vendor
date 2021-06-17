package com.foodtraffic.vendor.controller.employee;

import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.vendor.entity.employee.Employee;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Api(tags = "Employee")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/vendors/{vendorId}/employees")
    public List<EmployeeDto> getEmployeesByVendor(@PathVariable(name = "vendorId") Long vendorId,
                                                     @RequestParam(name = "type", defaultValue = "all") String type) {
        return employeeService.getEmployeeByVendor(vendorId, type);
    }

    @PostMapping("/vendors/{vendorId}/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(@PathVariable(name = "vendorId") Long vendorId, @RequestBody Employee employee) {
        return employeeService.createEmployee(vendorId, employee);
    }
}
