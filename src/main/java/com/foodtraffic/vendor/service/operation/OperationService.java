package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


public interface OperationService {
    List<OperationItemDto> getOperations(Long vendorId, String searchKey);

    List<OperationItemDto> getOperationItems(Long vendorId, String searchKey, LocalDate date);

    List<OperationItemDto> createWeek(Long vendorId);

    OperationItemDto updateOperationItem(Long vendorId, Long operationItemId, OperationItem operationItem, String accessToken);

    OperationItemDto createEvent(Long vendorId, @Valid OperationItem opItem, String accessToken);

    void deleteEvent(Long vendorId, Long operationItemId, String accessToken);
}
