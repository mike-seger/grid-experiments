package com.net128.app.jpa.adminux.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
//TODO replace by liquibase or sql file
public class DataLoader implements ApplicationRunner {

    private final PersonRepository personRepository;

    @Autowired
    public DataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void run(ApplicationArguments args) {
        personRepository.save(new Person(
            "firstName 1","lastName 1","address 1","city 1",Country.Switzerland));
        personRepository.save(new Person(
            "firstName 2","lastName 2","address 2","city 2",Country.Liechtenstein));
    }
}