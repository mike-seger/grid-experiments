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
public class CountryPopulation extends Identifiable {
	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private long population;

	@Column(nullable = false)
	private Double yChangePerc;

	@Column(nullable = false)
	private int netChange;

	@Column(nullable = false)
	private int perSqKm;

	@Column(nullable = false)
	private int area;

	@Column
	private Integer migrants;

	@Column
	private Double fertRate;

	@Column
	private Integer medAge;

	@Column
	private Double urbanPopPerc;

	@Column(nullable = false)
	private double worldSharePerc;
}
