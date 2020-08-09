package com.foodtraffic.vendor.repository.employee;

import com.foodtraffic.vendor.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByUserId(long userId);

    List<Employee> findAllByVendorId(long vendorId);

    List<Employee> findAllByVendorIdAndIsAdmin(long vendorId, boolean isAdmin);

    Employee findByVendorIdAndUserId(long vendorId, long userId);
}
