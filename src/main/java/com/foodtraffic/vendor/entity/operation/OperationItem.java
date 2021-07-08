package com.foodtraffic.vendor.entity.operation;

import lombok.Data;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@Entity(name = "OPERATION_ITEM")
public class OperationItem implements Comparable<OperationItem>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Override
    public int compareTo(OperationItem o) {
        return DayOfWeek.valueOf(dayOfWeek).getValue() - DayOfWeek.valueOf(o.dayOfWeek).getValue();
    }
}
