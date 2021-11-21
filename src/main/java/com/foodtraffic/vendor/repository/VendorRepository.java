package com.foodtraffic.vendor.repository;

import com.foodtraffic.vendor.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Page<Vendor> findAllByCityAndState(String city, String state, Pageable pageable);

    Page<Vendor> findAllByDisplayNameIgnoreCaseContaining(String name, Pageable pageable);

    boolean existsByUserName(String userName);
    
    @Query(value = "SELECT * FROM VENDOR v WHERE v.vendorId IN (SELECT fav.vendorId FROM FAVORITE fav WHERE fav.userId = :userId)", nativeQuery=true)
    Page<Vendor> findAllByUserFavorites(@Param("userId") Long userId, Pageable pageable);
    
}
