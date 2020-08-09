package com.foodtraffic.vendor.service;

import java.util.List;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.vendor.entity.Vendor;

public interface VendorService {

	List<VendorDto> getAllVendorsByName(String name);

	VendorDto getVendor(long id);

	VendorDto createVendor(Vendor vendor, String accessToken);

	VendorDto updateVendor(Vendor vendor);

	boolean checkVendorExists(String username, long id);

	List<VendorDto> getFavoritesForUser(String accessToken);

	boolean isFavorite(long id, String accessToken);
}
