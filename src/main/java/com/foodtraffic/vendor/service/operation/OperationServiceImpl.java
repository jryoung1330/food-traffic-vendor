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
    public List<OperationItemDto> getEvents(final Long vendorId, final String searchKey, LocalDate date) {
        List<OperationItem> events = null;
        if(searchKey.equals("month")) {
            events = operationItemRepo.findAllEventsInMonth(vendorId, date.getMonth().getValue());
        } else if(searchKey.equals("upcoming")) {
            events = operationItemRepo.findAllUpcomingEvents(vendorId, date);
        }
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
    public OperationItemDto createEvent(String accessToken, Long vendorId, @Valid OperationItem opItem) {
        // two types of events: 1) Holidays 2) Special Events
        // Holidays - day(s) where the vendor is closed
        // Special Events - days(s) where the vendor is open, but attending an event (not in the usual area)

        validateUserIsAdmin(vendorId, accessToken);

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
    public OperationItemDto updateOperationItem(String accessToken,
                                                Long vendorId,
                                                Long operationItemId,
                                                @Valid OperationItem operationItem) {
        validateUserIsAdmin(vendorId, accessToken);
        OperationItem opItem = operationItemRepo.getById(operationItemId);

        if(opItem.isEvent() && !operationItem.isEvent()
                || !opItem.isEvent() && operationItem.isEvent()
                || !opItem.getVendorId().equals(operationItem.getVendorId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to update one or more fields cannot be changed");
        }

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
    public void deleteEvent(String accessToken, Long vendorId, Long operationItemId) {
        validateUserIsAdmin(vendorId, accessToken);
        OperationItem opItem = operationItemRepo.getById(operationItemId);

        if (!opItem.getVendorId().equals(vendorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to update one or more fields cannot be changed");
        } else if (!opItem.isEvent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete operations that are not events");
        } else {
            operationItemRepo.delete(opItem);
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

    private void validateUserIsAdmin(Long vendorId, String accessToken) {
        UserDto user = AppUtil.getUser(userClient, accessToken);
        EmployeeDto emp = user.getEmployee();
        if(emp == null || emp.getVendorId() != vendorId || !emp.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges");
        }
    }
}
