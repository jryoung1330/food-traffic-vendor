package com.foodtraffic.vendor.service.employee;

import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.vendor.entity.employee.Employee;
import com.foodtraffic.vendor.repository.employee.EmployeeRepository;
import com.foodtraffic.vendor.service.VendorService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<EmployeeDto> getEmployeeByVendor(final long vendorId, final String type) {
        List<Employee> employees;

        if (!vendorService.checkVendorExists(null, vendorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        } else if ("admin".equals(type)) {
            employees = employeeRepo.findAllByVendorIdAndIsAdmin(vendorId, true);
        } else {
            employees = employeeRepo.findAllByVendorId(vendorId);
        }

        return modelMapper.map(employees, new TypeToken<List<EmployeeDto>>(){}.getType());
    }

    @Override
    public EmployeeDto createEmployee(final long vendorId, Employee employee) {
        HttpStatus status;
        String message;

        if (!vendorService.checkVendorExists(null, vendorId)) {
            status = HttpStatus.NOT_FOUND;
            message = "Resource does not exist";
        } else if (employeeRepo.existsById(employee.getId())) {
            status = HttpStatus.BAD_REQUEST;
            message = "This user is already an employee!";
        } else {
            employee.setVendorId(vendorId);
            employee = employeeRepo.saveAndFlush(employee);
            return modelMapper.map(employee, EmployeeDto.class);
        }
        throw new ResponseStatusException(status, message);
    }

    @Override
    public boolean isUserAnAdmin(long vendorId, long id) {
        Employee employee = employeeRepo.findByIdAndVendorId(id, vendorId);
        return employee.isAdmin();
    }
}
