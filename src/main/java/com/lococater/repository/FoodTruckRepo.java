package com.lococater.repository;

import com.lococater.entity.FoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodTruckRepo extends JpaRepository<FoodTruck, Long> {

    List<FoodTruck> findAllByCityAndState(String city, String state);
    List<FoodTruck> findAllByZipCode(Integer zipCode);
    List<FoodTruck> findAllByNameIgnoreCaseContaining(String name);
    boolean existsByNameAndCompany(String name, String company);
}
