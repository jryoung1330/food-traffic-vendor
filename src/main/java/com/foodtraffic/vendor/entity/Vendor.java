package com.foodtraffic.vendor.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="VENDOR")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Column(name = "IS_ONLINE")
    private boolean isOnline;
    
    @ManyToMany
    @JoinTable(
        name = "VENDOR_TAG",
        joinColumns = @JoinColumn(name = "VENDORID"),
        inverseJoinColumns = @JoinColumn(name = "TAGID")
    )
    @ToString.Exclude
    private List<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Vendor vendor = (Vendor) o;
        return Objects.equals(id, vendor.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
