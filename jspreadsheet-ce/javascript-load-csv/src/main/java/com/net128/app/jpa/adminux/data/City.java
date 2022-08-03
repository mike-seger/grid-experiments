package com.net128.app.jpa.adminux.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class City extends Identifiable {
	@Column(nullable = false)
	private String geonameId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String countryCode;

	@Column(nullable = false)
	private String countryNameEn;

	@Column(nullable = false)
	private long population;

	@Column(nullable = false)
	private LocalDate modificationDate;

	@Column(nullable = false)
	private String coordinates;
}
