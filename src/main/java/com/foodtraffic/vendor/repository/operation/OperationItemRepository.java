package com.foodtraffic.vendor.repository.operation;

import com.foodtraffic.vendor.entity.operation.OperationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationItemRepository extends JpaRepository<OperationItem, Long> {
    boolean existsByOperationIdAndId(Long operationId, Long operationItemId);
}
