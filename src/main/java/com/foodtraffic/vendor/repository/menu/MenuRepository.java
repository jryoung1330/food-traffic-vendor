package com.foodtraffic.vendor.repository.menu;

import com.foodtraffic.vendor.entity.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>{

	Optional<Menu> findByVendorId(Long vendorId);
	boolean existsByIdAndVendorId(Long id, Long vendorId);
}
