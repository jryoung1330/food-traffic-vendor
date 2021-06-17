package com.foodtraffic.vendor.entity.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "MENU_ITEM")
@Data
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUITEMID")
	private Long id;
	
	@Column(name = "MENUID")
	private Long menuId;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRICE")
	private Double price;
	
	@Column(name = "CALORIES")
	private Integer calories;
	
	@Column(name = "INGREDIENTS")
	private String ingredients;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "IS_VEGAN")
	@JsonProperty(value = "vegan")
	private Boolean isVegan;
	
	@Column(name = "IS_VEGETARIAN")
	@JsonProperty(value = "vegetarian")
	private Boolean isVegetarian;
	
	@Column(name = "IS_GLUTEN_FREE")
	@JsonProperty(value = "glutenFree")
	private Boolean isGlutenFree;
	
	@Column(name = "IS_DAIRY_FREE")
	@JsonProperty(value = "dairyFree")
	private Boolean isDairyFree;
	
	@Column(name = "CONTAINS_NUTS")
	private Boolean containsNuts;
}
