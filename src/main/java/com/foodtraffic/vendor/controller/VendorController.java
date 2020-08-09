package com.foodtraffic.vendor.controller;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.service.VendorService;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials="true")
@RestController
@Api(tags = "Vendor")
@RequestMapping("/vendors")
public class VendorController {

    @Autowired
    VendorService vendorService;

    @GetMapping
    public List<VendorDto> getVendors(@RequestParam(required = true) String name) {
        return vendorService.getAllVendorsByName(name);
    }

    @GetMapping("/{id}")
    public VendorDto getVendor(@PathVariable Long id) {
        return vendorService.getVendor(id);
    }

    @GetMapping("/check-vendor")
    public boolean checkVendor(@RequestParam(name = "user-name", required = false) String userName,
                               @RequestParam(name = "id", required = false,  defaultValue = "0") Long id) {
        return  vendorService.checkVendorExists(userName, id);
    }
    
    @GetMapping("/favorites")
    public List<VendorDto> getFavorites(@CookieValue(value = "_gid", defaultValue = "") String accessToken) {
    	return vendorService.getFavoritesForUser(accessToken);
    }
    
    @GetMapping("/{id}/favorites")
    public boolean isFavorite(@PathVariable Long id,
                              @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
    	return vendorService.isFavorite(id, accessToken);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendorDto createVendor(@RequestBody Vendor vendor,
                                  @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return vendorService.createVendor(vendor, accessToken);
    }

    @PutMapping("/{id}")
    public VendorDto updateVendor(@RequestBody Vendor vendor) {
        return vendorService.updateVendor(vendor);
    }
}
