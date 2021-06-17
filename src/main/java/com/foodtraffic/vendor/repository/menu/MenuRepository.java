package com.foodtraffic.vendor.repository.menu;

import com.foodtraffic.vendor.entity.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>{

	List<Menu> findAllByVendorId(Long vendorId);
	boolean existsByIdAndVendorId(Long id, Long vendorId);
}
