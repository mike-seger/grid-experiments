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
@Props.Identifiable
public class CountryPopulation extends Identifiable {
	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private long population;

	@Column(nullable = false)
	private Double yearChangePerc;

	@Column(nullable = false)
	private int netChange;

	@Column(nullable = false)
	private int popPerSqKm;

	@Column(nullable = false)
	private int areaSqKm;

	@Column
	private Integer migrants;

	@Column
	private Double fertRate;

	@Column
	private Integer medAge;

	@Column
	private Double popUrbanPerc;

	@Column(nullable = false)
	private double worldPerc;
}
