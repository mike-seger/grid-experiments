package com.net128.app.jpa.adminux.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
	List<City> findAllByOrderByPopulationDescCity();
	default List<City> findAll() { return findAllByOrderByPopulationDescCity(); }
}
