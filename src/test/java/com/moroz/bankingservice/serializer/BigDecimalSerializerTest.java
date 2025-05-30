package com.moroz.bankingservice.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setup() {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        MAPPER.registerModule(module);
    }

    record TestDTO(BigDecimal amount) {}

    @Test
    void shouldBeZeroWith2DecimalPlaces() throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(new TestDTO(BigDecimal.ZERO));
        assertEquals("{\"amount\":0.00}", json);
    }

    @Test
    void shouldBeWith2DecimalPlaces() throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(new TestDTO(BigDecimal.valueOf(123)));
        assertEquals("{\"amount\":123.00}", json);
    }

    @Test
    void shouldBeWith2DecimalPlacesWithInput1DecimalPlace() throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(new TestDTO(BigDecimal.valueOf(123.4)));
        assertEquals("{\"amount\":123.40}", json);
    }

    @Test
    void shouldBeWith2DecimalPlacesWithInput2DecimalPlaces() throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(new TestDTO(BigDecimal.valueOf(123.45)));
        assertEquals("{\"amount\":123.45}", json);
    }

    @Test
    void shouldBeNegativeWith2DecimalPlaces() throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(new TestDTO(BigDecimal.valueOf(-123)));
        assertEquals("{\"amount\":-123.00}", json);
    }
}
