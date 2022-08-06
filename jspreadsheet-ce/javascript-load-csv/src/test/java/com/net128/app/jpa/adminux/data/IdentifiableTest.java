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
public class IdentifiableTest {

    @Autowired
    private IdentifiableRepository identifiableRepository;

    @Test
    public void testSave() {
        long id = -1*System.currentTimeMillis();
        var car01 = createCar("01");
        var car01Saved = identifiableRepository.save(car01);
        assertEquals(car01.getBrand(), car01Saved.getBrand());
    }

    @Test
    public void testFind() {
        var car01 = createCar("02");
        var car01Saved = identifiableRepository.save(car01);
        car01.setId(car01Saved.getId());

        Example<Car> carExample01 = Example.of(car01);
        var car01Found = identifiableRepository.findOne(carExample01);
        assertTrue(car01Found.isPresent());
        assertEquals(car01Saved.getId(), car01Found.get().getId());

        car01.setBrand("other");
        Example<Car> carExample02 = Example.of(car01);
        var car02Found = identifiableRepository.findOne(carExample02);
        assertTrue(car02Found.isPresent());
        assertEquals(car01Saved.getId(), car02Found.get().getId());
    }

    private Car createCar(String id) {
        return new Car(id, 1234);
    }
}
