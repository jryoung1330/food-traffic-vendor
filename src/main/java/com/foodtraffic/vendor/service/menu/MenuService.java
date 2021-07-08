package com.foodtraffic.vendor.service.menu;

import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;
import com.foodtraffic.vendor.entity.menu.Menu;
import com.foodtraffic.vendor.entity.menu.MenuItem;

import java.util.List;

public interface MenuService {

	List<MenuDto> getAllMenusByVendor(long vendorId);

	MenuDto createMenu(long vendorId, Menu menu, String accessToken);

	MenuItemDto createMenuItem(long vendorId, long menuId, MenuItem menuItem, String accessToken);

	MenuDto updateMenu(long vendorId, long menuId, Menu menu, String accessToken);

	MenuItemDto updateMenuItem(long vendorId, long menuId, long menuItemId, MenuItem menuItem, String accessToken);

	void deleteMenu(long vendorId, long menuId, String accessToken);

	void deleteMenuItem(long vendorId, long menuId, long menuItemId, String accessToken);

	List<MenuItemDto> getTopSellingItems(Long vendorId, String accessToken);
}
