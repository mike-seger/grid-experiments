package com.net128.lib.spring.jpa.csv;

import com.net128.app.jpa.adminux.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

@SpringBootTest(classes= TestApplication.class)
@ActiveProfiles("test")
public class CsvServiceTest {

    @SuppressWarnings("unused")
    @Autowired
    private CsvService service;

    private final static String lt = "\n";

    @ParameterizedTest
    @CsvSource(value = {
        "EMPLOYEE |true  |true  |" +
            "name    department   salary ;" +
            "Henry   Sales        1234.0",
        "EMPLOYEE |true  |true  |"+
            "name    department   salary ;"+
            "Henry   Sales        1234.0 ;"+
            "Robert  Engineering  5080.0 ;",
        "EMPLOYEE |true  |false  |"+
            "name    department   salary ;"+
            "Henry   Sales        1234.0 ;"+
            "Robert  Engineering  5080.0 ;",
        "PET |true  |true  |"+
            "name   species  weight ;"+
            "Bobby  DOG      20.8",
        "PET |false |true  |"+
            "name   species  weight ;" +
            "Mimi   CAT      10.1",
        "PET |false |false |" +
            "name   species  weight ;" +
            "Emma   COW      220.8",
    }, delimiter = '|')
    public void testReadWriteCsv(final String entity, final boolean tabSeparated,
            final boolean deleteAll, final String input) throws IOException {
        var formattedInput = formatInput(input, tabSeparated);
        var inputLines = formattedInput.split(lt);
        var expected = Arrays.stream(inputLines)
            .map(l -> ".*"+l.trim()).collect(Collectors.toList());
        var oldCount = List.of(service.writeCsv(entity, tabSeparated).split(lt)).size();
        var nRead = submitCsv(entity, formattedInput, tabSeparated, deleteAll);
        assertEquals(inputLines.length-1, nRead);
        var result = List.of(service.writeCsv(entity, tabSeparated).split(lt));
        assertEquals(deleteAll?inputLines.length:oldCount+inputLines.length-1, result.size());
        if(!deleteAll) {
            var header = result.get(0);
            result = new ArrayList<>(result.subList(oldCount, result.size()));
            result.add(0, header);
        }
        assertLinesMatch(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "EMPLOYEE |true  |true  |RuntimeJsonMappingException|String \"INVALID\": not a valid `double`|" +
            "department   name    salary ;" +
            "Sales        Henry   INVALID",
        "EMPLOYEE |true  |true  |ValidationException|must be greater than or equal to 1|"+
            "department   name    salary ;"+
            "Sales        Henry   1234.0 ;"+
            "Engineering  Robert  0 ;",
        "EMPLOYEE |true  |false  |RuntimeJsonMappingException|department is a very long name|"+
            "department is a very long name  name    salary ;"+
            "Sales        Henry   1234.0 ;"+
            "Engineering  Robert  5080.0 ;",
        "PET |true  |true  |ValidationException|'must be less than or equal to 9999', propertyPath=weight|"+
            "name   species  weight ;"+
            "Walter  DOG      10220.8",
        "PET |true  |true  |RuntimeJsonMappingException|Species` from String \"LION\"|"+
            "name   species  weight ;"+
            "Clarence  LION      220.8",
    }, delimiter = '|')
    public void testInvalidCsv(final String entity, final boolean tabSeparated,
            final boolean deleteAll, final String exception, final String msgPattern, final String input) {
        var e = Assertions.assertThrows(Exception.class, () -> submitCsv(entity, formatInput(input, tabSeparated), tabSeparated, deleteAll));
        assertEquals(exception, e.getClass().getSimpleName());
        assertThat(e.getMessage()).contains(msgPattern);
    }

    private int submitCsv(String entity, String csvString,
            boolean tabSeparated, boolean deleteAll) throws IOException {
        return service.readCsv(new ByteArrayInputStream(csvString.getBytes()), entity, tabSeparated, deleteAll);
    }

    private String formatInput(String input, boolean tabSeparated) {
        var separator = tabSeparated?"\t":",";
        return input.replaceAll(" {2,}", separator).replaceAll(" *; *", lt);
    }
}
