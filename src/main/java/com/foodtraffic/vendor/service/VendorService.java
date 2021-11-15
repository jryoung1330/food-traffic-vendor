package com.foodtraffic.vendor.service;

import java.util.List;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;

public interface VendorService {

	List<VendorDto> getAllVendorsByName(String name);

	List<VendorDto> getAllVendorsByLocation(String city, String state);

	VendorDto getVendor(long id);

	VendorDto createVendor(String accessToken, Vendor vendor);

	VendorDto updateVendor(String accessToken, long id, Vendor vendor);

	boolean checkVendorExists(String username);

	boolean checkVendorExists(long id);

	List<VendorDto> getFavoritesForUser(String accessToken);

	boolean isFavorite(String accessToken, long id);

}
