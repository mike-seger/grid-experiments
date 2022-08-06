package com.net128.lib.spring.jpa.csv;

import com.net128.app.jpa.adminux.TestApplication;
import com.net128.app.jpa.adminux.util.JsonAware;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

@SpringBootTest(classes= TestApplication.class)
@ActiveProfiles("test")
@Slf4j
public class JpaServiceTest implements JsonAware {

    @SuppressWarnings("unused")
    @Autowired
    private JpaService service;

    @Test
    public void testGetConfiguration() {
        log.info(toJson(service.getConfiguration()));
    }
}
