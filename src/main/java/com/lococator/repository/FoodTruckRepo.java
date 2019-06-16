package com.lococator.repository;

import com.lococator.entity.FoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodTruckRepo extends JpaRepository<FoodTruck, Long> {

    List<FoodTruck> findAllByCityAndState(String city, String state);
    List<FoodTruck> findAllByZipCode(Integer zipCode);
    Optional<FoodTruck> findByName(String name);
    boolean existsByNameAndCompany(String name, String company);
}
