package com.foodtraffic.vendor.entity.operation;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "OPERATION")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPERATIONID")
    private Long id;

    @Column(name = "VENDORID")
    private Long vendorId;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "OPERATIONID", updatable=false)
    @ToString.Exclude
    private List<OperationItem> operationItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Operation operation = (Operation) o;
        return Objects.equals(id, operation.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
