package com.foodtraffic.vendor.repository.operation;

import com.foodtraffic.vendor.entity.operation.OperationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OperationItemRepository extends JpaRepository<OperationItem, Long> {
    boolean existsByOperationIdAndId(Long operationId, Long operationItemId);

    List<OperationItem> findAllByOperationIdAndIsEventFalse(long operationId);

    @Query(value = "select * from OPERATION_ITEM oi" +
            " where oi.operationid = ?1 and oi.event_start_date <= ?2 and oi.event_end_date >= ?2",
            nativeQuery = true)
    Optional<OperationItem> findByOperationIdAndBetweenEventDates(long operationId, LocalDate day);
}
