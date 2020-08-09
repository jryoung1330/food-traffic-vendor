package com.foodtraffic.vendor.entity.menu;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Set;

@Entity
@Table(name = "MENU")
@Data
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUID")
	@Min(0)
	private Long id;

	@Column(name = "VENDORID")
	private Long vendorId;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "MENUID", updatable=false)
	private Set<MenuItem> menuItems;

}
