package com.foodtraffic.vendor.repository.operation;

import com.foodtraffic.vendor.entity.operation.OperationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OperationItemRepository extends JpaRepository<OperationItem, Long> {
    boolean existsByVendorIdAndId(Long vendorId, Long operationItemId);

    List<OperationItem> findAllByVendorIdAndIsEventFalse(Long operationId);

    @Query(value = "select * from OPERATION_ITEM oi" +
            " where oi.vendorid = ?1 and oi.event_start_date <= ?2 and oi.event_end_date >= ?2",
            nativeQuery = true)
    Optional<OperationItem> findByVendorIdAndBetweenEventDates(long vendorId, LocalDate day);

    @Query(value = "select * from OPERATION_ITEM oi" +
            " where oi.vendorId = ?1 and oi.is_event = true and extract('MONTH' from oi.event_start_date) = ?2" +
            " order by oi.event_start_date",
            nativeQuery = true)
    List<OperationItem> findAllEventsInMonth(long vendorId, int month);

    @Query(value = "select * from OPERATION_ITEM oi" +
            " where oi.vendorId = ?1 and oi.is_event = true and oi.event_start_date >= ?2" +
            " order by oi.event_start_date",
            nativeQuery = true)
    List<OperationItem> findAllUpcomingEvents(long vendorId, LocalDate startDate);
}
