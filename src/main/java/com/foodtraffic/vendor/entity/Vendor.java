package com.foodtraffic.vendor.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name="VENDOR")
@Data
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="VENDORID")
    private Long id;

    @Column(name="USER_NAME")
    @NotNull
    @Size(min = 4, max = 25)
    private String userName;

    @Column(name="DISPLAY_NAME")
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String displayName;

    @Column(name="COMPANY")
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
    @NotNull
    @Size(min = 2, max = 2)
    private String state;

    @Column(name = "COUNTY")
    @NotNull
    @NotEmpty
    @Size(max = 50)
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
    @NotNull
    private Long owner;
    
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "VENDOR_TAG",
        joinColumns = { @JoinColumn(name = "VENDORID") },
        inverseJoinColumns = { @JoinColumn(name = "TAGID") }
    )
    private List<Tag> tags;
}
