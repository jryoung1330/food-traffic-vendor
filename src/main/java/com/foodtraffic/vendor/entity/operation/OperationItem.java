package com.foodtraffic.vendor.entity.operation;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity(name = "OPERATION_ITEM")
public class OperationItem {

    @Id
    @Column(name = "OPERATIONITEMID")
    private Long id;

    @Column(name = "OPERATIONID")
    private Long operationId;

    @Column(name = "DAY_OF_WEEK")
    private String dayOfWeek;

    @Column(name = "IS_CLOSED")
    private boolean isClosed;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "OPERATION_DATE")
    private LocalDate date;

    @Column(name = "OPEN_TIME")
    private String openTime;

    @Column(name = "CLOSE_TIME")
    private String closeTime;
}
