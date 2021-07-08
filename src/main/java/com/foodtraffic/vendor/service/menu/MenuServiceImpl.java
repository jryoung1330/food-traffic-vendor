package com.foodtraffic.vendor.service.menu;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.vendor.entity.menu.Menu;
import com.foodtraffic.vendor.entity.menu.MenuItem;
import com.foodtraffic.vendor.repository.menu.MenuItemRepository;
import com.foodtraffic.vendor.repository.menu.MenuRepository;
import com.foodtraffic.vendor.service.VendorService;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepo;

    @Autowired
    private MenuItemRepository menuItemRepo;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<MenuDto> getAllMenusByVendor(final long vendorId) {
        if (vendorService.checkVendorExists(null, vendorId)) {
            List<Menu> menus = menuRepo.findAllByVendorId(vendorId);
            return modelMapper.map(menus, new TypeToken<List<MenuDto>>(){}.getType());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public MenuDto createMenu(final long vendorId, Menu menu, final String accessToken) {
        validateRequest(vendorService.checkVendorExists(null, vendorId), vendorId, accessToken);

        menu.setId(0L);
        menu.setVendorId(vendorId);
        menu = menuRepo.save(menu);
        return modelMapper.map(menu, MenuDto.class);
    }

    @Override
    public MenuDto updateMenu(final long vendorId, final long menuId, Menu menu, final String accessToken) {
        validateRequest(menuRepo.existsByIdAndVendorId(menuId, vendorId), vendorId, accessToken);

        menu.setId(menuId);
        menu.setVendorId(vendorId);
        menu = menuRepo.save(menu);
        return modelMapper.map(menu, MenuDto.class);
    }

    @Override
    public void deleteMenu(final long vendorId, final long menuId, final String accessToken) {
        validateRequest(menuRepo.existsByIdAndVendorId(menuId, vendorId), vendorId, accessToken);
        menuRepo.deleteById(menuId);
    }

    @Override
    public MenuItemDto createMenuItem(final long vendorId, final long menuId, MenuItem menuItem, final String accessToken) {
        validateRequest((vendorService.checkVendorExists(null, vendorId) || menuRepo.existsById(menuId)), vendorId, accessToken);

        menuItem.setId(0L);
        menuItem.setMenuId(menuId);
        menuItem = menuItemRepo.save(menuItem);
        return modelMapper.map(menuItem, MenuItemDto.class);
    }

    @Override
    public MenuItemDto updateMenuItem(final long vendorId, final long menuId, final long menuItemId, MenuItem menuItem,
                                      final String accessToken) {
        validateRequest((menuRepo.existsByIdAndVendorId(menuId, vendorId) || menuItemRepo.existsByIdAndMenuId(menuItemId, menuId)), vendorId, accessToken);

        menuItem = mergeMenuItem(menuItem, menuItemId);
        menuItem.setMenuId(menuId);
        menuItem.setId(menuItemId);
        menuItem = menuItemRepo.save(menuItem);
        return modelMapper.map(menuItem, MenuItemDto.class);
    }

    @Override
    public void deleteMenuItem(final long vendorId, final long menuId, final long menuItemId, final String accessToken) {
        validateRequest((menuRepo.existsByIdAndVendorId(menuId, vendorId) || menuItemRepo.existsByIdAndMenuId(menuItemId, menuId)), vendorId, accessToken);
        menuItemRepo.deleteById(menuItemId);
    }

    @Override
    public List<MenuItemDto> getTopSellingItems(Long vendorId, String accessToken) {
        // TODO: revise when order data is available
        List<MenuItem> menuItems = menuItemRepo.findAllByVendorId(vendorId);
        List<MenuItem> topThree = new ArrayList<>();
        for(int i=0; i<3 && i<menuItems.size(); i++) {
            topThree.add(menuItems.get(i));
        }
        return modelMapper.map(topThree, new TypeToken<List<MenuItemDto>>(){}.getType());
    }

    /*
     * helper methods
     */

    private boolean isAdmin(Long vendorId, String accessToken) {
        UserDto user = userClient.checkAccessHeader(accessToken);
        return employeeService.isUserAnAdmin(vendorId, user.getId());
    }

    private MenuItem mergeMenuItem(MenuItem updatedItem, Long menuItemId) {
        MenuItem mergedItem = menuItemRepo.getById(menuItemId);

        try {
            for (Field f : updatedItem.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(updatedItem) != null) {
                    f.set(mergedItem, f.get(updatedItem));
                }
            }
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return mergedItem;
    }

    private void validateRequest(boolean resourcesExist, long vendorId, String accessToken) {
        if (!resourcesExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        } else if (!isAdmin(vendorId, accessToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges");
        }
    }
}
