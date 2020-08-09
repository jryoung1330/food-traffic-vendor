package com.foodtraffic.vendor.service.employee;

import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.vendor.entity.employee.Employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> getEmployeeByVendor(long vendorId, String type);

    EmployeeDto createEmployee(long vendorId, Employee employee);

    boolean isUserAnAdmin(long vendorId, long userId);
}
