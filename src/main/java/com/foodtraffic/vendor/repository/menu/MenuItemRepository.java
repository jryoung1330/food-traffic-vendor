package com.foodtraffic.vendor.repository.menu;

import com.foodtraffic.vendor.entity.menu.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    MenuItem getById(Long menuItemId);
    boolean existsByIdAndMenuId(Long id, Long menuId);

}
