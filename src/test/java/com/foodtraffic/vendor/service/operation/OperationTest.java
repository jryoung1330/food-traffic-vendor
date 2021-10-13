package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.vendor.entity.operation.Operation;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.repository.operation.OperationItemRepository;
import com.foodtraffic.vendor.repository.operation.OperationRepository;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
public class OperationTest {

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private OperationRepository operationRepo;

    @Mock
    private OperationItemRepository operationItemRepo;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OperationServiceImpl operationService;

    private static final String ACCESS_TOKEN = "abcdefg123";

    @Test
    public void givenSearchKeyEqualsWeek_whenGetOperations_thenReturnWeek() {
        List<OperationItem> opItems = mockWeek();
        when(operationRepo.findOneByVendorId(100L)).thenReturn(mockOperation());
        when(operationItemRepo.findAllByOperationIdAndIsEventFalse(0)).thenReturn(opItems);
        when(operationItemRepo.findByOperationIdAndBetweenEventDates(anyLong(), anyObject())).thenReturn(Optional.empty());
        OperationDto operation = operationService.getOperations(100L, "week");
        assertEquals(7, operation.getOperationItems().size());
    }

    @Test
    public void givenSearchKeyEqualsWeek_whenGetOperations_thenReturnSortedWeek() {
        List<OperationItem> opItems = mockWeek();
        Collections.swap(opItems, 0, 6); // swap Monday and Sunday
        when(operationRepo.findOneByVendorId(100L)).thenReturn(mockOperation());
        when(operationItemRepo.findAllByOperationIdAndIsEventFalse(0)).thenReturn(opItems);
        when(operationItemRepo.findByOperationIdAndBetweenEventDates(anyLong(), anyObject())).thenReturn(Optional.empty());
        OperationDto operation = operationService.getOperations(100L, "week");
        assertEquals(modelMapper.map(mockWeek(), new TypeToken<List<OperationItemDto>>(){}.getType()), operation.getOperationItems());
    }

    @Test
    public void givenEventDuringWeek_whenGetOperations_thenReturnWeekWithEvent() {
        List<OperationItem> opItems = mockWeek();
        when(operationRepo.findOneByVendorId(100L)).thenReturn(mockOperation());
        when(operationItemRepo.findAllByOperationIdAndIsEventFalse(0)).thenReturn(opItems);
        when(operationItemRepo.findByOperationIdAndBetweenEventDates(anyLong(), anyObject())).thenReturn(Optional.empty());
        when(operationItemRepo.findByOperationIdAndBetweenEventDates(anyLong(), eq(LocalDate.now()))).thenReturn(mockEvent());
        OperationDto operation = operationService.getOperations(100L, "week");
        assertEquals("Test Event", getEvent(operation.getOperationItems()).getEventName());
    }

    @Test
    public void givenNonAdmin_whenCreateEvent_thenThrow403Exception() {
        UserDto nonAdmin = mockUser();
        nonAdmin.getEmployee().setAdmin(false);
        when(userClient.checkAccessHeader(anyString())).thenReturn(nonAdmin);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(100L, 0L, new OperationItem(), ACCESS_TOKEN));
        assertEquals(HttpStatus.FORBIDDEN, rse.getStatus());
    }

    @Test
    public void givenNoStartDate_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent().get();
        opItem.setEventStartDate(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(100L, 0L, opItem, ACCESS_TOKEN));
        assertEquals("Start Date and End Date are required for events", rse.getReason());
    }

    @Test
    public void givenNoEndDate_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent().get();
        opItem.setEventEndDate(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(100L, 0L, opItem, ACCESS_TOKEN));
        assertEquals("Start Date and End Date are required for events", rse.getReason());
    }

    @Test
    public void givenNotClosedAndNullOpenTime_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent().get();
        opItem.setClosed(false);
        opItem.setOpenTime(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(100L, 0L, opItem, ACCESS_TOKEN));
        assertEquals("Open Time and Close Time are required if not closed", rse.getReason());
    }

    @Test
    public void givenValidItem_whenCreateEvent_thenReturnNewEvent() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(operationItemRepo.save(any())).thenReturn(mockEvent().get());
        OperationItemDto opItem = operationService.createEvent(100L, 0L, mockEvent().get(), ACCESS_TOKEN);
        assertNotNull(opItem);
    }

    private Optional<Operation> mockOperation() {
        Operation operation = new Operation();
        operation.setId(0L);
        operation.setVendorId(100L);
        operation.setOperationItems(mockWeek());
        return Optional.of(operation);
    }

    private List<OperationItem> mockWeek() {
        List<OperationItem> opItems = new ArrayList<>();
        for (int i=0; i<7; i++) {
            OperationItem opItem = new OperationItem();
            opItem.setOperationId(0L);
            opItem.setId((long) i);
            opItem.setOpenTime("9:00");
            opItem.setCloseTime("17:00");
            opItem.setEvent(false);
            opItem.setDayOfWeek(DayOfWeek.of(i+1).name());
            opItems.add(opItem);
        }
        return opItems;
    }

    private Optional<OperationItem> mockEvent() {
        OperationItem opItem = new OperationItem();
        opItem.setId(10L);
        opItem.setOperationId(0L);
        opItem.setEvent(true);
        opItem.setClosed(true);
        opItem.setEventStartDate(LocalDate.now());
        opItem.setEventEndDate(LocalDate.now());
        opItem.setEventName("Test Event");
        return Optional.of(opItem);
    }

    private OperationItemDto getEvent(List<OperationItemDto> opItems) {
        for (OperationItemDto opItem : opItems) {
            if(opItem.getEventStartDate() != null && opItem.getEventStartDate().equals(LocalDate.now())) {
                return  opItem;
            }
        }
        return null;
    }

    private UserDto mockUser() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("test");
        user.setFirstName("Test");
        user.setLastName("Test");

        EmployeeDto emp = new EmployeeDto();
        emp.setEmployeeId(1L);
        emp.setVendorId(100L);
        emp.setAdmin(true);
        emp.setAssociate(true);

        user.setEmployee(emp);
        return user;
    }
}
