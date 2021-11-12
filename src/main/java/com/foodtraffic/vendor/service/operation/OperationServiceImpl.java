package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.util.AppUtil;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.repository.operation.OperationItemRepository;
import com.foodtraffic.vendor.repository.operation.OperationRepository;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OperationRepository operationRepo;

    @Autowired
    private OperationItemRepository operationItemRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserClient userClient;

    @Override
    public List<OperationItemDto> getOperations(final Long vendorId, String searchKey) {
        List<OperationItem> returnList = new ArrayList<>();
        List<OperationItem> operationItems = operationItemRepo.findAllByVendorIdAndIsEventFalse(vendorId);
        Collections.sort(operationItems);

        if("week".equals(searchKey)) {
            returnList = applyEvents(operationItems);
        } else if("3-day".equals(searchKey)) {
            for(int i=0; i<3; i++) {
                int day = LocalDateTime.now().plusDays(i).getDayOfWeek().getValue();
                returnList.add(operationItems.get(day-1));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request. Check the search key and try again");
        }

        return modelMapper.map(returnList, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    @Override
    public List<OperationItemDto> getOperationItems(final Long vendorId, final String searchKey, LocalDate date) {
        List<OperationItemDto> returnList = null;
        if(searchKey.equals("month")) {
            returnList = getEventsForMonth(vendorId, date.getMonth().name());
        } else if(searchKey.equals("upcoming")) {
            returnList = getUpcomingEvents(vendorId, date);
        }
        return returnList;
    }

    public List<OperationItemDto> getEventsForMonth(final Long vendorId, String month) {
        Month searchKey;
        try {
            searchKey = Month.of(Integer.parseInt(month));
        } catch (NumberFormatException e) {
            searchKey = Month.valueOf(month.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        List<OperationItem> events = operationItemRepo.findAllEventsInMonth(vendorId, searchKey.getValue());
        return modelMapper.map(events, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    public List<OperationItemDto> getUpcomingEvents(final Long vendorId, LocalDate startDate) {
        List<OperationItem> events = operationItemRepo.findAllUpcomingEvents(vendorId, startDate);
        return modelMapper.map(events, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    @Override
    public List<OperationItemDto> createWeek(Long vendorId) {
        List<OperationItem> opItems = operationItemRepo.findAllByVendorIdAndIsEventFalse(vendorId);
        if(opItems.isEmpty()) {
            opItems = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                OperationItem opItem = new OperationItem();
                opItem.setVendorId(vendorId);
                opItem.setDayOfWeek(DayOfWeek.of(i + 1).name());
                opItem.setOpenTime("9:00");
                opItem.setCloseTime("17:00");
                opItems.add(opItem);
            }
            opItems = operationItemRepo.saveAll(opItems);
        }
        return modelMapper.map(opItems, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    @Override
    public OperationItemDto createEvent(Long vendorId, @Valid OperationItem opItem, String accessToken) {
        // two types of events: 1) Holidays 2) Special Events
        // Holidays - day(s) where the vendor is closed
        // Special Events - days(s) where the vendor is open, but attending an event (not in the usual area)

        validateRequest(true, vendorId, accessToken);

        if(opItem.getEventStartDate() == null || opItem.getEventEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start Date and End Date are required for events");
        } else if(opItem.getEventEndDate().isBefore(opItem.getEventStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End Date cannot be before Start Date");
        }

        if(opItem.isClosed()) {
            opItem.setOpenTime(null);
            opItem.setCloseTime(null);
        } else if(Strings.isBlank(opItem.getOpenTime()) || Strings.isBlank(opItem.getCloseTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Open Time and Close Time are required if not closed");
        }

        opItem.setId(null);

        opItem.setDayOfWeek(null);
        opItem.setEvent(true);
        opItem.setVendorId(vendorId);
        opItem = operationItemRepo.save(opItem);
        return modelMapper.map(opItem, OperationItemDto.class);
    }

    @Override
    public OperationItemDto updateOperationItem(Long vendorId, Long operationItemId,
                                                @Valid OperationItem operationItem, String accessToken) {
        validateRequest(operationItemRepo.existsByVendorIdAndId(vendorId, operationItemId), vendorId, accessToken);

        if (operationItem.isEvent()) {
            operationItem.setDayOfWeek(null);
        }

        if (operationItem.isClosed()) {
            operationItem.setOpenTime(null);
            operationItem.setCloseTime(null);
        }

        operationItem = operationItemRepo.save(operationItem);
        return modelMapper.map(operationItem, OperationItemDto.class);
    }

    @Override
    public void deleteEvent(Long vendorId, Long operationItemId, String accessToken) {
        validateRequest(operationItemRepo.existsByVendorIdAndId(vendorId, operationItemId), vendorId, accessToken);

        OperationItem opItem = operationItemRepo.getOne(operationItemId);
        if(opItem.isEvent()) {
            operationItemRepo.delete(opItem);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete operations that are not events");
        }
    }

    /*
     * helper methods
     */

    private List<OperationItem> applyEvents(List<OperationItem> opItems) {
        List<OperationItem> newOps = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for(OperationItem opItem : opItems) {
            int diff = DayOfWeek.valueOf(opItem.getDayOfWeek()).getValue() - today.getDayOfWeek().getValue();
            OperationItem replacement = getEvent(opItem.getVendorId(), today.plusDays(diff));
            newOps.add(Objects.requireNonNullElse(replacement, opItem));
        }

        return newOps;
    }

    private OperationItem getEvent(Long vendorId, LocalDate day) {
        Optional<OperationItem> option = operationItemRepo.findByVendorIdAndBetweenEventDates(vendorId, day);
        if(option.isPresent()) {
            OperationItem opItem = new OperationItem();
            modelMapper.map(option.get(), opItem);
            opItem.setDayOfWeek(day.getDayOfWeek().name());
            return opItem;
        }
        return null;
    }

    private boolean isAdmin(Long vendorId, String accessToken) {
        UserDto user = AppUtil.getUser(userClient, accessToken);
        EmployeeDto emp = user.getEmployee();
        return emp != null && emp.getVendorId() == vendorId && emp.isAdmin();
    }

    private void validateRequest(boolean resourcesExist, Long vendorId, String accessToken) {
        if(!resourcesExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        } else if(!isAdmin(vendorId, accessToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges");
        }
    }
}
