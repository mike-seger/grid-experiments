package com.net128.app.jpa.adminux.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface City47kRepository extends JpaRepository<City47k, Long> {
	List<City47k> findAllByOrderByPopulationDesc();
	default List<City47k> findAll() { return findAllByOrderByPopulationDesc(); }
}
