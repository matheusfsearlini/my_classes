package br.com.oauth.exception;

import java.io.IOException;
import java.time.LocalDate;
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
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalDateDeserializer.class);

    @Override
    public LocalDate deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String dataStr = json.getValueAsString();
        try {
            LOG.info("REALIZANDO A CONVERSAO DE {} PARA DATA", dataStr);
            return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            LOG.error("FALHA NA CONVERSAO UTILIZANDO O FORMATO dd/MM/yyyy, APLICANDO CONVERSAO PELO FORMATO MM/yy");
            try {
                return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("M/[uuuu][uu]"));
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("FORMATO INVALIDO DE DATA (" + dataStr + ") NO CAMPO " + json.getCurrentName() + ", UTILIZE O PADRAO dd/MM/yyyy HH:mm OU MM/yy");
            }
        }
    }

}