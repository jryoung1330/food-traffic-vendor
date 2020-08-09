package com.foodtraffic.vendor.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="TAG")
@Data
public class Tag {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TAGID")
	private Long id;
	
	@Column(name="NAME")
	private String name;

	@ManyToMany(mappedBy="tags")
	private List<Vendor> foodTrucks;
}
