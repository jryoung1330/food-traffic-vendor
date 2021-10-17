package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.util.AppUtil;
import com.foodtraffic.vendor.entity.operation.Operation;
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
        List<OperationItem> opItems = new ArrayList<>();
        Optional<Operation> option = operationRepo.findOneByVendorId(vendorId);

        if(option.isPresent()) {
            List<OperationItem> operationItems = operationItemRepo.findAllByOperationIdAndIsEventFalse(option.get().getId());
            Collections.sort(operationItems);

            if("week".equals(searchKey)) {
                opItems = applyEvents(operationItems);
            } else if("3-day".equals(searchKey)) {
                for(int i=0; i<3; i++) {
                    int day = LocalDateTime.now().plusDays(i).getDayOfWeek().getValue();
                    opItems.add(operationItems.get(day-1));
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request. Check the search key and try again");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        }

        return modelMapper.map(opItems, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    @Override
    public OperationDto createWeek(Long vendorId) {
        Optional<Operation> operation = operationRepo.findOneByVendorId(vendorId);
        if(operation.isEmpty()) {
            Operation op = modelMapper.map(createOperation(vendorId), Operation.class);
            List<OperationItem> opItems = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                OperationItem opItem = new OperationItem();
                opItem.setOperationId(op.getId());
                opItem.setDayOfWeek(DayOfWeek.of(i+1).name());
                opItem.setOpenTime("9:00");
                opItem.setCloseTime("17:00");
                opItems.add(opItem);
            }
            opItems = operationItemRepo.saveAll(opItems);
            op.setOperationItems(opItems);
            return modelMapper.map(op, OperationDto.class);
        } else {
            return modelMapper.map(operation.get(), OperationDto.class);
        }
    }

    public List<OperationItemDto> getEventsForMonth(final Long vendorId, Long operationId, String month) {
        Month searchKey;
        try {
            searchKey = Month.of(Integer.parseInt(month));
        } catch (NumberFormatException e) {
            searchKey = Month.valueOf(month.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        List<OperationItem> events = operationItemRepo.findAllEventsInMonth(operationId, searchKey.getValue());
        return modelMapper.map(events, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    public List<OperationItemDto> getUpcomingEvents(final Long vendorId, final Long operationId, LocalDate startDate) {
        List<OperationItem> events = operationItemRepo.findAllUpcomingEvents(operationId, startDate);
        return modelMapper.map(events, new TypeToken<List<OperationItemDto>>(){}.getType());
    }

    @Override
    public OperationItemDto updateOperationItem(Long vendorId, Long operationId, Long operationItemId,
                                                @Valid OperationItem operationItem, String accessToken) {
        validateRequest(operationItemRepo.existsByOperationIdAndId(operationId, operationItemId), vendorId, accessToken);

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
    public OperationItemDto createEvent(Long vendorId, Long operationId, @Valid OperationItem opItem, String accessToken) {
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
        opItem.setOperationId(operationId);
        opItem = operationItemRepo.save(opItem);
        return modelMapper.map(opItem, OperationItemDto.class);
    }

    @Override
    public List<OperationItemDto> getOperationItems(Long vendorId, Long operationId, String searchKey, LocalDate date) {
        List<OperationItemDto> returnList = null;
        if(searchKey.equals("month")) {
            returnList = getEventsForMonth(vendorId, operationId, date.getMonth().name());
        } else if(searchKey.equals("upcoming")) {
            returnList = getUpcomingEvents(vendorId, operationId, date);
        }
        return returnList;
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

    public OperationDto createOperation(Long vendorId) {
        Operation operation = new Operation();
        operation.setVendorId(vendorId);
        operation = operationRepo.saveAndFlush(operation);
        return modelMapper.map(operation, OperationDto.class);
    }

    /*
     * helper methods
     */

    private List<OperationItem> applyEvents(List<OperationItem> opItems) {
        List<OperationItem> newOps = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for(OperationItem opItem : opItems) {
            int diff = DayOfWeek.valueOf(opItem.getDayOfWeek()).getValue() - today.getDayOfWeek().getValue();
            OperationItem replacement = getEvent(opItem.getOperationId(), today.plusDays(diff));
            newOps.add(Objects.requireNonNullElse(replacement, opItem));
        }

        return newOps;
    }

    private OperationItem getEvent(Long operationId, LocalDate day) {
        Optional<OperationItem> option = operationItemRepo.findByOperationIdAndBetweenEventDates(operationId, day);
        if(option.isPresent()) {
            OperationItem opItem = new OperationItem();
            modelMapper.map(option.get(), opItem);
            opItem.setDayOfWeek(day.getDayOfWeek().name());
            return opItem;
        }
        return null;
    }
}
