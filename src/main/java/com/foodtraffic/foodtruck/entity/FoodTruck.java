package com.foodtraffic.foodtruck.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="FOOD_TRUCK")
@Data
public class FoodTruck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="FOODTRUCKID")
    @Min(0)
    private Long id;

    @Column(name="FOOD_TRUCK_NAME")
    @NotNull
    @NotEmpty
    @Size(max = 25)
    private String foodTruckName;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="COMPANY")
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String company;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name="LATITUDE")
    private Double latitude;

    @Column(name = "STREET_ADDRESS")
    @Size(max = 300)
    private String streetAddress;

    @Column(name = "CITY")
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String city;

    @Column(name = "STATE")
    @NotEmpty
    @NotNull
    @Size(min = 2, max = 2)
    private String state;

    @Column(name = "COUNTY")
    @NotNull
    @Size(max = 50)
    private String county;

    @Column(name = "ZIP_CODE")
    private int zipCode;

    @Column(name = "LOCATION_DETAILS")
    private String locationDetails;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private Integer status;
}
