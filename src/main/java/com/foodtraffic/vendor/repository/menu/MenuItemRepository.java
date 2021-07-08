package com.foodtraffic.vendor.repository.menu;

import com.foodtraffic.vendor.entity.menu.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    MenuItem getById(Long menuItemId);

    boolean existsByIdAndMenuId(Long id, Long menuId);

    @Query(value = "select * from MENU_ITEM mi" +
            " where mi.menuid in (select menuid from MENU m where m.vendorId = ?1)",
            nativeQuery = true)
    List<MenuItem> findAllByVendorId(Long vendorId);
}
