package com.net128.app.jpa.adminux.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryPopulationRepository extends JpaRepository<CountryPopulation, Long> {
	List<CountryPopulation> findAllByOrderByPopulationDesc();
	default List<CountryPopulation> findAll() { return findAllByOrderByPopulationDesc(); }
}
