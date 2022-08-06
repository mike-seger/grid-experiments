package com.net128.lib.spring.jpa.csv;

import com.net128.app.jpa.adminux.TestApplication;
import com.net128.app.jpa.adminux.util.JsonAware;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
