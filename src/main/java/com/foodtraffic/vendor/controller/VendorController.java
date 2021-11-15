package com.foodtraffic.vendor.controller;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.service.VendorService;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@Api(tags = "Vendor")
@RequestMapping("/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @GetMapping
    public List<VendorDto> getVendors(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String city,
                                      @RequestParam(required = false)  String state) {
        if(name != null) {
            return vendorService.getAllVendorsByName(name);
        } else {
            return vendorService.getAllVendorsByLocation(city, state);
        }
    }

    @GetMapping("/{id}")
    public VendorDto getVendor(@PathVariable Long id) {
        return vendorService.getVendor(id);
    }

    @GetMapping("/check-vendor")
    public boolean checkVendor(@RequestParam(name = "user-name", required = false) String username,
                               @RequestParam(name = "id", required = false,  defaultValue = "0") Long id) {
        return username != null ? vendorService.checkVendorExists(username) : vendorService.checkVendorExists(id);
    }
    
    @GetMapping("/favorites")
    public List<VendorDto> getFavorites(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken) {
    	return vendorService.getFavoritesForUser(accessToken);
    }

    @GetMapping("/{id}/favorites")
    public boolean isFavorite(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                              @PathVariable Long id) {
    	return vendorService.isFavorite(accessToken, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendorDto createVendor(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                                  @RequestBody Vendor vendor) {
        return vendorService.createVendor(accessToken, vendor);
    }

    @PutMapping("/{id}")
    public VendorDto updateVendor(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                                  @PathVariable(name="id") Long id,
                                  @RequestBody Vendor vendor) {
        return vendorService.updateVendor(accessToken, id, vendor);
    }
}
