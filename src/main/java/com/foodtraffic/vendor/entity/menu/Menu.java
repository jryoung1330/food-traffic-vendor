package com.foodtraffic.vendor.entity.menu;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MENU")
@Data
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUID")
	private Long id;

	@Column(name = "VENDORID")
	private Long vendorId;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "MENUID", updatable=false)
	private List<MenuItem> menuItems;

}
