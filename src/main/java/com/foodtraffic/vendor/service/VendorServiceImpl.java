package com.foodtraffic.vendor.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.util.AppUtil;
import com.foodtraffic.vendor.entity.Vendor;
import com.foodtraffic.vendor.entity.VendorStatus;
import com.foodtraffic.vendor.entity.employee.Employee;
import com.foodtraffic.vendor.repository.FavoriteRepository;
import com.foodtraffic.vendor.repository.VendorRepository;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepo;
    
    @Autowired
    private FavoriteRepository favoriteRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserClient userClient;

    @Override
    public List<VendorDto> getAllVendorsByName(final String name) {
        List<Vendor> vendors = vendorRepo.findAllByDisplayNameIgnoreCaseContaining(name);
        return modelMapper.map(vendors, new TypeToken<List<VendorDto>>(){}.getType());
    }

    @Override
    public List<VendorDto> getAllVendorsByLocation(String city, String state) {
        List<Vendor> vendors = vendorRepo.findAllByCityAndState(city, state);
        return modelMapper.map(vendors, new TypeToken<List<VendorDto>>(){}.getType());
    }

    @Override
    public VendorDto getVendor(final long id) {
        Optional<Vendor> optionalVendor = vendorRepo.findById(id);
        if (!optionalVendor.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(optionalVendor.get(), VendorDto.class);
    }

    @Override
    public VendorDto createVendor(Vendor vendor, final String accessToken) {
        // check user access
        UserDto user = AppUtil.getUser(userClient, accessToken);

        if(isValid(vendor)) {

            // create vendor
            vendor.setId(0L);
            vendor.setUserName(vendor.getUserName().toLowerCase());
            vendor.setStatus(VendorStatus.HOLD.name());
            vendor.setOwner(user.getId());
            vendor = vendorRepo.saveAndFlush(vendor);

            associateEmployee(vendor.getId(), user.getId());

            return modelMapper.map(vendor, VendorDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    @Override
    public VendorDto updateVendor(final long id, Vendor vendor, final String accessToken) {
        UserDto user = AppUtil.getUser(userClient, accessToken);
        if(isValid(id, vendor, user)) {
            vendor = vendorRepo.saveAndFlush(vendor);
        }
        return modelMapper.map(vendor, VendorDto.class);
    }

    @Override
    public boolean checkVendorExists(final String username, final long id) {
        if (id > 0) {
            return vendorRepo.existsById(id);
        } else if (username != null) {
            return vendorRepo.existsByUserName(username);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    
    @Override
	public List<VendorDto> getFavoritesForUser(final String accessToken) {
        UserDto user = AppUtil.getUser(userClient, "Bearer " + accessToken);
        List<Vendor> vendors = vendorRepo.findAllByUserFavorites(user.getId());
        return modelMapper.map(vendors, new TypeToken<List<VendorDto>>(){}.getType());
	}
    
    @Override
	public boolean isFavorite(final long id, final String accessToken) {
    	UserDto user = AppUtil.getUser(userClient, accessToken);
		return favoriteRepo.existsByVendorIdAndUserId(id, user.getId());
	}

    /*
     * helper methods
     */

    private boolean isValid(Vendor vendor){
        try {
            return (vendor.getUserName().matches("[a-zA-Z0-9_]+")
                    && vendor.getUserName().length() >= 4
                    && vendor.getUserName().length() <= 25
                    && !vendorRepo.existsByUserName(vendor.getUserName().toLowerCase())
                    && vendor.getDisplayName().length() > 0
                    && vendor.getDisplayName().length() <= 50
                    && vendor.getCity() != null
                    && vendor.getState() != null
                    && vendor.getDescription().length() <= 300);
        } catch (NullPointerException e) {
        	return false;
        }
    }

    private void associateEmployee(long vendorId, long userId) {
        Employee employee = new Employee();
        employee.setUserId(userId);
        employee.setAssociate(true);
        employee.setAdmin(true);
        employeeService.createEmployee(vendorId, employee);
    }

    private boolean isValid(final long id, Vendor vendor, UserDto user) {
        checkVendorExists(null, id);

        if (vendor.getId() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
        } else if (!employeeService.isUserAnAdmin(id, user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        return true;
    }
}
