package com.foodtraffic.vendor.service;

import java.util.List;

import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.model.response.Payload;
import com.foodtraffic.vendor.entity.Vendor;

public interface VendorService {

	Payload<List<VendorDto>> getAllVendorsByName(String name, int page, int size);

	Payload<List<VendorDto>> getAllVendorsByLocation(String city, String state, int page, int size);

	VendorDto getVendor(long id);

	VendorDto createVendor(String accessToken, Vendor vendor);

	VendorDto updateVendor(String accessToken, long id, Vendor vendor);

	boolean checkVendorExists(String username);

	boolean checkVendorExists(long id);

	Payload<List<VendorDto>> getFavoritesForUser(String accessToken, int page, int size);

	boolean isFavorite(String accessToken, long id);

}
