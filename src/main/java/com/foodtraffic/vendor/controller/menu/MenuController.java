package com.foodtraffic.vendor.controller.menu;

import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;
import com.foodtraffic.vendor.entity.menu.Menu;
import com.foodtraffic.vendor.entity.menu.MenuItem;
import com.foodtraffic.vendor.service.menu.MenuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.66:3000"})
@Api(tags = "Menu")
@RestController
@RequestMapping("/vendors/{vendorId}/menus")
public class MenuController {

	@Autowired
	private MenuService menuService;
	
	@GetMapping
	public MenuDto getMenuForVendor(@PathVariable(name = "vendorId") Long vendorId) {
		return menuService.getMenuByVendor(vendorId);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuDto createMenu(@PathVariable(name = "vendorId") Long vendorId,
                              @RequestBody Menu menu,
                              @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenu(vendorId, menu, accessToken);
	}
	
	@PostMapping("/{menuId}/menu-items")
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuItemDto createMenuItem(@PathVariable(name = "vendorId") Long vendorId,
                                      @PathVariable(name = "menuId") Long menuId,
                                      @RequestBody MenuItem menuItem,
                                      @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenuItem(vendorId, menuId, menuItem, accessToken);
	}
	
	@PutMapping("/{menuId}")
	public MenuDto createMenu(@PathVariable(name = "vendorId") Long vendorId,
                              @PathVariable(name = "menuId") Long menuId,
                              @RequestBody Menu menu,
                              @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenu(vendorId, menuId, menu, accessToken);
	}
	
	@PutMapping("/{menuId}/menu-items/{menuItemId}")
	public MenuItemDto updateMenuItem(@PathVariable(name = "vendorId") Long vendorId,
                                      @PathVariable(name = "menuId") Long menuId,
                                      @PathVariable(name = "menuItemId") Long menuItemId,
                                      @RequestBody MenuItem menuItem,
                                      @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenuItem(vendorId, menuId, menuItemId, menuItem, accessToken);
	}
	
	@DeleteMapping("/{menuId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenu(@PathVariable(name = "vendorId") Long vendorId,
						   @PathVariable(name = "menuId") Long menuId,
						   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenu(vendorId, menuId, accessToken);
	}
	
	@DeleteMapping("/{menuId}/menu-items/{menuItemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenuItem(@PathVariable(name = "vendorId") Long vendorId,
							   @PathVariable(name = "menuId") Long menuId,
							   @PathVariable(name = "menuItemId") Long menuItemId,
							   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenuItem(vendorId, menuId, menuItemId, accessToken);
	}
	
}
