package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;


public interface OperationService {
    OperationDto getOperations(long vendorId, String searchKey);

    OperationDto createWeek(Long vendorId);

    OperationItemDto updateOperationItem(Long vendorId, Long operationId, Long operationItemId, OperationItem operationItem, String accessToken);

    OperationItemDto createEvent(Long vendorId, Long operationId, OperationItem operationItem, String accessToken);
}
