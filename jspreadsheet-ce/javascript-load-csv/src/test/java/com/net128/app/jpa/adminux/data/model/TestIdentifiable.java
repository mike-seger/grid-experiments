package com.net128.app.jpa.adminux.data.model;

import com.net128.lib.spring.jpa.csv.util.Props;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
@Data
public abstract class TestIdentifiable {
	@Props.Hidden
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TestIdentifiable)) return false;
		TestIdentifiable that = (TestIdentifiable) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
