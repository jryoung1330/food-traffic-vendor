package com.foodtraffic.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtraffic.vendor.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
	boolean existsByVendorIdAndUserId(Long vendorId, Long userId);
}
