package com.net128.app.jpa.adminux.data.controller;

import com.net128.app.jpa.adminux.data.Person;
import com.net128.app.jpa.adminux.data.PersonRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/person")
public class PersonController {
	private final PersonRepository personRepository;

	public PersonController(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping
	public List<Person> findPersons() {
		return personRepository.findAll();
	}

	@PostMapping
	public void savePerson(@RequestBody Person person) {
		personRepository.save(person);
	}
}
