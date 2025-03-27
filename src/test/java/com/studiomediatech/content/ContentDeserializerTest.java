package com.studiomediatech.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ContentDeserializerTest {

    @Test
    void ensureDeserializesJsonContent() throws JsonMappingException, JsonProcessingException {

        var json = """
                {
                  "contents": [{
                      "mimeType": "text/vnd.content.description",
                      "content": "Kontakt"
                    }, {
                      "mimeType": "text/vnd.content.description",
                      "locale": "en",
                      "content": "Contact"
                    }]
                }
                """;

        record Dto(List<Content> contents) {
            // OK
        }

        Dto value = new ObjectMapper().readValue(json, Dto.class);
        assertThat(value).isNotNull().hasFieldOrProperty("contents");

        assertThat(value.contents()).isNotEmpty().containsExactlyInAnyOrder(
                new Content("text/vnd.content.description", "Kontakt"),
                new Content("text/vnd.content.description", "Contact", Locale.forLanguageTag("en")));

    }
}
