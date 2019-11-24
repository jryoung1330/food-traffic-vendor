package com.foodtraffic.foodtruck.repository;

import com.foodtraffic.foodtruck.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUserId(Long userId);
}
