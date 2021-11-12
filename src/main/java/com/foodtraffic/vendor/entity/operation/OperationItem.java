package com.foodtraffic.vendor.entity.operation;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "OPERATION_ITEM")
public class OperationItem implements Comparable<OperationItem>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPERATIONITEMID")
    private Long id;

    @Column(name = "VENDORID")
    private Long vendorId;

    @Column(name = "DAY_OF_WEEK")
    private String dayOfWeek;

    @Column(name = "IS_CLOSED")
    private boolean isClosed;

    @Column(name = "OPEN_TIME")
    private String openTime;

    @Column(name = "CLOSE_TIME")
    private String closeTime;

    @Column(name = "IS_EVENT")
    private boolean isEvent;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_URL")
    private String eventUrl;

    @Column(name = "EVENT_START_DATE")
    private LocalDate eventStartDate;

    @Column(name = "EVENT_END_DATE")
    private LocalDate eventEndDate;

    @Override
    public int compareTo(OperationItem o) {
        return DayOfWeek.valueOf(dayOfWeek).getValue() - DayOfWeek.valueOf(o.dayOfWeek).getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OperationItem that = (OperationItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
