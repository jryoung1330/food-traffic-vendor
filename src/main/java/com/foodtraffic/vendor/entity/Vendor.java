package com.foodtraffic.vendor.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="VENDOR")
@Data
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="VENDORID")
    private Long id;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="COMPANY")
    private String company;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name="LATITUDE")
    private Double latitude;

    @Column(name = "STREET_ADDRESS")
    private String streetAddress;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "COUNTY")
    private String county;

    @Column(name = "ZIP_CODE")
    private Integer zipCode;

    @Column(name = "LOCATION_DETAILS")
    private String locationDetails;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "OWNER")
    private Long owner;

    @Column(name = "PROFILE_IMAGE")
    private String profileImage;
    
    @ManyToMany
    @JoinTable(
        name = "VENDOR_TAG",
        joinColumns = @JoinColumn(name = "VENDORID"),
        inverseJoinColumns = @JoinColumn(name = "TAGID")
    )
    private List<Tag> tags;
}
