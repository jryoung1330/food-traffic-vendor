package com.foodtraffic.vendor.entity.operation;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
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
    private List<OperationItem> operationItems;
}
