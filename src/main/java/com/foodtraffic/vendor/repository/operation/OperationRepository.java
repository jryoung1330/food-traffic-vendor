package com.foodtraffic.vendor.repository.operation;

import com.foodtraffic.vendor.entity.operation.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Optional<Operation> findOneByVendorId(long vendorId);

    @Query(value = "select * from OPERATION op" +
            " INNER JOIN OPERATION_ITEM oi" +
            " on op.operationid = oi.operationid and oi.is_event = false" +
            " where op.vendorid = ?1",
    nativeQuery = true)
    Optional<Operation> getHoursOfOperation(long vendorId);
}
