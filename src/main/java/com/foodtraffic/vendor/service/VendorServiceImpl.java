package com.foodtraffic.vendor.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.model.dto.VendorDto;
import com.foodtraffic.model.response.Payload;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private static final int MAX_PAGE_SIZE = 25;

    @Override
    public Payload<List<VendorDto>> getAllVendorsByName(final String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        Page<Vendor> results = vendorRepo.findAllByDisplayNameIgnoreCaseContaining(name, pageable);
        List<VendorDto> vendors = modelMapper.map(results.getContent(), new TypeToken<List<VendorDto>>(){}.getType());
        return new Payload<>(vendors, results, "/vendors?name=" + name + "&");
    }

    @Override
    public Payload<List<VendorDto>> getAllVendorsByLocation(String city, String state, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        Page<Vendor> results = vendorRepo.findAllByCityAndState(city, state, pageable);
        List<VendorDto> vendors = modelMapper.map(results.getContent(), new TypeToken<List<VendorDto>>(){}.getType());
        return new Payload<>(vendors, results, "/vendors?city=" + city + "&state=" + state + "&");
    }

    @Override
    public VendorDto getVendor(final long id) {
        Optional<Vendor> optionalVendor = vendorRepo.findById(id);
        if (optionalVendor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(optionalVendor.get(), VendorDto.class);
    }

    @Override
    public VendorDto createVendor(final String accessToken, Vendor vendor) {
        // check user access
        UserDto user = AppUtil.getUser(userClient, accessToken);

        if(isValid(vendor)) {

            // create vendor
            vendor.setId(0L);
            vendor.setUserName(vendor.getUserName().toLowerCase());
            vendor.setStatus(VendorStatus.HOLD.name());
            vendor.setOwner(user.getId());
            vendor = vendorRepo.saveAndFlush(vendor);

            associateEmployee(vendor.getId(), vendor.getOwner());

            return modelMapper.map(vendor, VendorDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    @Override
    public VendorDto updateVendor(final String accessToken, final long id, Vendor vendor) {
        UserDto user = AppUtil.getUser(userClient, accessToken);
        EmployeeDto emp = user.getEmployee();

        if (vendor.getId() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if(!vendorRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (emp == null || emp.getVendorId() != id || !emp.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else {
            vendor = vendorRepo.saveAndFlush(vendor);
        }

        return modelMapper.map(vendor, VendorDto.class);
    }

    @Override
    public boolean checkVendorExists(final String username) {
        return username != null && vendorRepo.existsByUserName(username);
    }

    @Override
    public boolean checkVendorExists(final long id) {
        return id > 0 && vendorRepo.existsById(id);
    }
    
    @Override
	public Payload<List<VendorDto>> getFavoritesForUser(final String accessToken, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        UserDto user = AppUtil.getUser(userClient, accessToken);
        Page<Vendor> results = vendorRepo.findAllByUserFavorites(user.getId(), pageable);
        List<VendorDto> vendors = modelMapper.map(results.getContent(), new TypeToken<List<VendorDto>>(){}.getType());
        return new Payload<>(vendors, results, "/vendors/favorites?");
	}
    
    @Override
	public boolean isFavorite(final String accessToken, final long id) {
    	UserDto user = AppUtil.getUser(userClient, accessToken);
		return favoriteRepo.existsByVendorIdAndUserId(id, user.getId());
	}

    /*
     * helper methods
     */

    private boolean isValid(Vendor vendor) {
        try {
            return (vendor.getUserName().matches("[a-zA-Z0-9_]+")
                    && vendor.getUserName().length() >= 4
                    && vendor.getUserName().length() <= 25
                    && !vendorRepo.existsByUserName(vendor.getUserName().toLowerCase())
                    && vendor.getDisplayName().length() > 0
                    && vendor.getDisplayName().length() <= 50
                    && vendor.getCity() != null
                    && vendor.getState() != null
                    && (vendor.getDescription() == null || vendor.getDescription().length() <= 300));
        } catch (NullPointerException e) {
        	return false;
        }
    }

    private void associateEmployee(long vendorId, long userId) {
        Employee employee = new Employee();
        employee.setId(userId);
        employee.setAssociate(true);
        employee.setAdmin(true);
        employeeService.createEmployee(vendorId, employee);
    }
}
