package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.repository.operation.OperationItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private OperationItemRepository operationItemRepo;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OperationServiceImpl operationService;

    private static final String ACCESS_TOKEN = "test";

    @Test
    public void givenSearchKeyEqualsWeek_whenGetOperations_thenReturnWeek() {
        List<OperationItem> opItems = mockWeek();
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(opItems);
        when(operationItemRepo.findByVendorIdAndBetweenEventDates(anyLong(), any())).thenReturn(Optional.empty());
        List<OperationItemDto> operations = operationService.getOperations(100L, "week");
        assertEquals(7, operations.size());
    }

    @Test
    public void givenSearchKeyEqualsWeek_whenGetOperations_thenReturnSortedWeek() {
        List<OperationItem> opItems = mockWeek();
        Collections.swap(opItems, 0, 6); // swap Monday and Sunday
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(opItems);
        when(operationItemRepo.findByVendorIdAndBetweenEventDates(anyLong(), any())).thenReturn(Optional.empty());
        List<OperationItemDto> operations = operationService.getOperations(100L, "week");
        assertEquals(modelMapper.map(mockWeek(), new TypeToken<List<OperationItemDto>>(){}.getType()), operations);
    }

    @Test
    public void givenEventDuringWeek_whenGetOperations_thenReturnWeekWithEvent() {
        List<OperationItem> opItems = mockWeek();
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(opItems);
        when(operationItemRepo.findByVendorIdAndBetweenEventDates(anyLong(), any())).thenReturn(Optional.empty());
        when(operationItemRepo.findByVendorIdAndBetweenEventDates(anyLong(), eq(LocalDate.now()))).thenReturn(Optional.of(mockEvent()));
        List<OperationItemDto> operations = operationService.getOperations(100L, "week");
        assertEquals("Test Event", getEvent(operations).getEventName());
    }

    @Test
    public void given3Day_whenGetOperations_thenReturn3Days() {
        List<OperationItem> opItems = mockWeek();
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(opItems);
        List<OperationItemDto> result = operationService.getOperations(100L, "3-day");
        assertEquals(3, result.size());
        assertEquals(LocalDate.now().getDayOfWeek().name(), result.get(0).getDayOfWeek());
    }

    @Test
    public void givenInvalidSearchKey_whenGetOperations_thenThrowException() {
        List<OperationItem> opItems = mockWeek();
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(opItems);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.getOperations(100L, "7-day"));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenMonth_whenGetEvents_thenReturnEventsForMonth() {
        LocalDate now = LocalDate.now();
        List<OperationItem> eventsInMonth = List.of(mockEvent());
        when(operationItemRepo.findAllEventsInMonth(100L, now.getMonth().getValue())).thenReturn(eventsInMonth);
        List<OperationItemDto> result = operationService.getEvents(100L, "month", now);
        assertEquals(now.getMonth(), result.get(0).getEventStartDate().getMonth());
    }

    @Test
    public void givenUpcoming_whenGetEvents_thenReturnAllUpcomingEvents() {
        LocalDate now = LocalDate.now();
        List<OperationItem> upcomingEvents = List.of(mockEvent());
        when(operationItemRepo.findAllUpcomingEvents(100L, now)).thenReturn(upcomingEvents);
        List<OperationItemDto> result = operationService.getEvents(100L, "upcoming", now);
        assertEquals(now, result.get(0).getEventStartDate());
    }

    @Test
    public void givenNonAdmin_whenCreateEvent_thenThrow403Exception() {
        UserDto nonAdmin = mockUser();
        nonAdmin.getEmployee().setAdmin(false);
        when(userClient.checkAccessHeader(anyString())).thenReturn(nonAdmin);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.createEvent(ACCESS_TOKEN, 100L, new OperationItem()));
        assertEquals(HttpStatus.FORBIDDEN, rse.getStatus());
    }

    @Test
    public void givenNoStartDate_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent();
        opItem.setEventStartDate(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(ACCESS_TOKEN, 100L, opItem));
        assertEquals("Start Date and End Date are required for events", rse.getReason());
    }

    @Test
    public void givenNoEndDate_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent();
        opItem.setEventEndDate(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(ACCESS_TOKEN, 100L, opItem));
        assertEquals("Start Date and End Date are required for events", rse.getReason());
    }

    @Test
    public void givenNotClosedAndNullOpenTime_whenCreateEvent_thenThrow400Exception() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        OperationItem opItem = mockEvent();
        opItem.setClosed(false);
        opItem.setOpenTime(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> operationService.createEvent(ACCESS_TOKEN, 100L, opItem));
        assertEquals("Open Time and Close Time are required if not closed", rse.getReason());
    }

    @Test
    public void givenValidItem_whenCreateEvent_thenReturnNewEvent() {
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(operationItemRepo.save(any())).thenReturn(mockEvent());
        OperationItemDto opItem = operationService.createEvent(ACCESS_TOKEN, 100L, mockEvent());
        assertNotNull(opItem);
    }

    @Test
    public void givenValidEvent_whenUpdateOperationItem_thenReturnUpdatedItem() {
        OperationItem event = mockEvent();
        event.setEventName("Holiday");
        when(operationItemRepo.getById(10L)).thenReturn(mockEvent());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(operationItemRepo.save(any())).thenReturn(event);
        OperationItemDto result = operationService.updateOperationItem(ACCESS_TOKEN, 100L, 10L,event);
        assertEquals(event.getEventName(), result.getEventName());
    }

    @Test
    public void givenValidOpItem_whenUpdateOperationItem_thenReturnUpdatedItem() {
        OperationItem opItem = mockOpItem();
        opItem.setCloseTime("20:00");
        when(operationItemRepo.getById(10L)).thenReturn(mockOpItem());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        when(operationItemRepo.save(any())).thenReturn(opItem);
        OperationItemDto result = operationService.updateOperationItem(ACCESS_TOKEN, 100L, 10L, opItem);
        assertEquals(opItem.getCloseTime(), result.getCloseTime());
    }

    @Test
    public void givenConversionFromEventToOpItem_whenUpdateOperationItem_thenThrowException() {
        OperationItem event = mockEvent();
        when(operationItemRepo.getById(10L)).thenReturn(mockOpItem());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.updateOperationItem(ACCESS_TOKEN, 100L, 10L, event));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenConversionFromOpItemToEvent_whenUpdateOperationItem_thenThrowException() {
        OperationItem opItem = mockOpItem();
        when(operationItemRepo.getById(10L)).thenReturn(mockEvent());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.updateOperationItem(ACCESS_TOKEN, 100L, 10L, opItem));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenInvalidEvent_whenUpdateOperationItem_thenThrowException() {
        OperationItem event = mockEvent();
        event.setEventName("Holiday");
        event.setVendorId(11L);
        when(operationItemRepo.getById(10L)).thenReturn(mockEvent());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.updateOperationItem(ACCESS_TOKEN, 100L, 10L, event));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenValidEvent_whenDeleteEvent_thenCallDelete() {
        when(operationItemRepo.getById(10L)).thenReturn(mockEvent());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        operationService.deleteEvent(ACCESS_TOKEN, 100L, 10L);
        verify(operationItemRepo, times(1)).delete(any());
    }

    @Test
    public void givenOpItem_whenDeleteEvent_thenThrowException() {
        when(operationItemRepo.getById(10L)).thenReturn(mockOpItem());
        when(userClient.checkAccessHeader(anyString())).thenReturn(mockUser());
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> operationService.deleteEvent(ACCESS_TOKEN, 100L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenVendorId_whenCreateWeek_thenReturnWeek() {
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(new ArrayList<>());
        when(operationItemRepo.saveAll(any())).thenReturn(mockWeek());
        List<OperationItemDto> week = operationService.createWeek(100L);
        assertEquals(7, week.size());
    }

    @Test
    public void givenWeekAlreadyExists_whenCreateWeek_thenSaveIsNotCalled() {
        when(operationItemRepo.findAllByVendorIdAndIsEventFalse(100L)).thenReturn(mockWeek());
        operationService.createWeek(100L);
        verify(operationItemRepo, times(0)).saveAll(any());
    }

    private List<OperationItem> mockWeek() {
        List<OperationItem> opItems = new ArrayList<>();
        for (int i=0; i<7; i++) {
            OperationItem opItem = new OperationItem();
            opItem.setVendorId(100L);
            opItem.setId((long) i);
            opItem.setOpenTime("9:00");
            opItem.setCloseTime("17:00");
            opItem.setEvent(false);
            opItem.setDayOfWeek(DayOfWeek.of(i+1).name());
            opItems.add(opItem);
        }
        return opItems;
    }

    private OperationItem mockOpItem() {
        OperationItem opItem = new OperationItem();
        opItem.setVendorId(100L);
        opItem.setId(10L);
        opItem.setEvent(false);
        opItem.setOpenTime("9:00");
        opItem.setCloseTime("16:00");
        return opItem;
    }

    private OperationItem mockEvent() {
        OperationItem event = new OperationItem();
        event.setId(10L);
        event.setVendorId(100L);
        event.setEvent(true);
        event.setClosed(true);
        event.setEventStartDate(LocalDate.now());
        event.setEventEndDate(LocalDate.now());
        event.setEventName("Test Event");
        return event;
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
