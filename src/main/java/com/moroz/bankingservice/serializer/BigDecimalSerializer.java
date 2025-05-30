package com.moroz.bankingservice.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (bigDecimal != null) {
            jsonGenerator.writeNumber(bigDecimal.setScale(2, RoundingMode.HALF_UP));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
