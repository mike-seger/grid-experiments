package com.net128.app.jpa.adminux.data;

import com.net128.lib.spring.jpa.csv.util.Props;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Props.Sortable
public class City47k extends Identifiable {
	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private long population;

	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private String countryIso;

	@Column(nullable = false)
	private double latitude;

	@Column(nullable = false)
	private double longitude;
}
