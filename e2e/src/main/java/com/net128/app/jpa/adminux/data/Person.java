package com.net128.app.jpa.adminux.data;

import com.net128.lib.spring.jpa.csv.util.Props;
import lombok.*;

import javax.persistence.*;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Person {
	@Props.Hidden
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long myId;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Country country;
}
