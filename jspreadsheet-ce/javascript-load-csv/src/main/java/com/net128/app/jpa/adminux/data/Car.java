package com.net128.app.jpa.adminux.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id","firstName","lastName","address","city","country"})
public class Car extends Identifiable {
	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private double price;
}
