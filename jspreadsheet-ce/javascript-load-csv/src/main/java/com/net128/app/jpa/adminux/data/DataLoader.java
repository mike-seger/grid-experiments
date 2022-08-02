package com.net128.app.jpa.adminux.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
//TODO replace by liquibase or sql file
public class DataLoader implements ApplicationRunner {

    private final PersonRepository personRepository;
    private final CarRepository carRepository;

    @Autowired
    public DataLoader(PersonRepository personRepository, CarRepository carRepository) {
        this.personRepository = personRepository;
        this.carRepository = carRepository;
    }

    public void run(ApplicationArguments args) {
        carRepository.save(new Car("Ferrari", 1234));
        carRepository.save(new Car("Honda", 123));

        personRepository.save(new Person(
            "firstName 1","lastName 1","address 1","city 1",Country.Switzerland));
        personRepository.save(new Person(
            "firstName 2","lastName 2","address 2","city 2",Country.Liechtenstein));
    }
}