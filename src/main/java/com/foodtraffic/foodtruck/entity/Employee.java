package com.foodtraffic.foodtruck.entity;

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

    @Column(name = "USERID")
    private Long userId;

    @Column(name = "FOODTRUCKID")
    private Long foodTruckId;

    @Column(name = "IS_ASSOCIATE")
    private boolean isAssociate;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin;

    @Column(name = "IS_OWNER")
    private boolean isOwner;

    @Column(name = "STATUS")
    private Integer status;
}
