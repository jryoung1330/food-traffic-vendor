package com.foodtraffic.vendor.service;

import java.util.List;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;

public interface VendorService {

	List<VendorDto> getAllVendorsByName(String name);

	List<VendorDto> getAllVendorsByLocation(String city, String state);

	VendorDto getVendor(long id);

	VendorDto createVendor(Vendor vendor, String accessToken);

	VendorDto updateVendor(long id, Vendor vendor, String accessToken);

	boolean checkVendorExists(String username, long id);

	List<VendorDto> getFavoritesForUser(String accessToken);

	boolean isFavorite(long id, String accessToken);

}
