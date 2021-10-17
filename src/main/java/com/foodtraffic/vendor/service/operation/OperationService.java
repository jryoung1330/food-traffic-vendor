package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;

import java.time.LocalDate;
import java.util.List;


public interface OperationService {
    List<OperationItemDto> getOperations(Long vendorId, String searchKey);

    OperationDto createWeek(Long vendorId);

    List<OperationItemDto> getUpcomingEvents(Long vendorId, Long operationId, LocalDate startDate);

    OperationItemDto updateOperationItem(Long vendorId, Long operationId, Long operationItemId, OperationItem operationItem, String accessToken);

    OperationItemDto createEvent(Long vendorId, Long operationId, OperationItem operationItem, String accessToken);

    List<OperationItemDto> getOperationItems(Long vendorId, Long operationId, String searchKey, LocalDate date);
}
