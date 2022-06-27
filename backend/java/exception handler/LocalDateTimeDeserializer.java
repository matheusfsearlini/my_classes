package br.com.oauth.exception;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@Component
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalDateDeserializer.class);

    @Override
    public LocalDateTime deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String dataStr = json.getValueAsString();
        try {
            LOG.info("REALIZANDO A CONVERSAO DE {} PARA DATA", dataStr);
            return LocalDateTime.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (DateTimeParseException exceptionTry1) {
            LOG.error("FALHA NA CONVERSAO UTILIZANDO O FORMATO dd/MM/yyyy HH:mm, APLICANDO CONVERSAO PELO FORMATO dd/MM/yyyy");
            try {
                return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            } catch (DateTimeParseException exceptionTry2) {
                try {
                    return LocalDateTime.parse(dataStr);
                } catch (DateTimeParseException exceptionTry3) {
                    throw new IllegalArgumentException("FORMATO INVALIDO DE DATA (" + dataStr + ") NO CAMPO " + json.getCurrentName() + ", UTILIZE O PADRAO dd/MM/yyyy HH:mm OU dd/MM/yyyy");
                }
            }
        }
    }
}
