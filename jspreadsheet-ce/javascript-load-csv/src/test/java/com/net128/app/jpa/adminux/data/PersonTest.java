package com.net128.app.jpa.adminux.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class PersonTest {
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testSave() {
        var person01 = createPerson("01");
        var person01Saved = personRepository.save(person01);
        assertEquals(person01.getAddress(), person01Saved.getAddress());
        var person01Found = personRepository.findById(person01.getMyId());
        assertTrue(person01Found.isPresent());
        assertEquals(person01Saved.getMyId(), person01Found.get().getMyId());
    }

    @Test
    public void testFind() {
        var person01 = createPerson("02");
        var person01Saved = personRepository.save(person01);
        person01.setMyId(person01Saved.getMyId());

        Example<Person> personExample01 = Example.of(person01);
        var person01Found = personRepository.findOne(personExample01);
        assertTrue(person01Found.isPresent());
        assertEquals(person01Saved.getMyId(), person01Found.get().getMyId());

        person01.setAddress("other");
        Example<Person> personExample02 = Example.of(person01);
        var person02Found = personRepository.findOne(personExample02);
        assertTrue(person02Found.isPresent());
        assertEquals(person01Saved.getMyId(), person02Found.get().getMyId());
    }

    private Person createPerson(String id) {
        return new Person(null, id, id, id+1234, id, Country.Austria);
    }
}
