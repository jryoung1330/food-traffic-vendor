package com.foodtraffic.vendor.entity.employee;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEE")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEEID")
    private Long id;

    @Column(name = "VENDORID")
    private Long vendorId;

    @Column(name = "IS_ASSOCIATE")
    private boolean isAssociate;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin;
}
