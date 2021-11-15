package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


public interface OperationService {
    List<OperationItemDto> getOperations(Long vendorId, String searchKey);

    List<OperationItemDto> getEvents(Long vendorId, String searchKey, LocalDate date);

    List<OperationItemDto> createWeek(Long vendorId);

    OperationItemDto updateOperationItem(String accessToken, Long vendorId, Long operationItemId, OperationItem operationItem);

    OperationItemDto createEvent(String accessToken, Long vendorId, @Valid OperationItem opItem);

    void deleteEvent(String accessToken, Long vendorId, Long operationItemId);
}
