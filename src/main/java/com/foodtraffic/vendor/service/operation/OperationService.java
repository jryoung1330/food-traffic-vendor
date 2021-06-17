package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationDto;

public interface OperationService {
    OperationDto getOperations(long vendorId, String searchKey);
}
