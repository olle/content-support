package com.studiomediatech.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContentTest {

    @Test
    public void ensureHasPrettyToString() throws Exception {

        Content c1 = new Content("type", "value");
        String string1 = c1.toString();
        assertThat(string1).as("Missing information").contains("mimeType=type");
        assertThat(string1).as("Missing information").contains("content=value");
        assertThat(string1).as("Must not have information").doesNotContain("locale=");

        Content c2 = new Content("type", "value", Locale.ENGLISH);
        String string2 = c2.toString();
        assertThat(string2).as("Missing information").contains("locale=en");
    }

    @Test
    public void ensureReadsProperContentFromJSON() throws Exception {

        String json = "{\"mimeType\": \"text/vnd.content.appicon\", \"content\": \"some-app-icon\", \"locale\": \"sv\"}";

        Content content = new ObjectMapper().readValue(json.getBytes(), Content.class);

        assertThat(content.getMimeType()).isEqualTo(MimeType.TEXT_APPICON_VAL);
        assertThat(content.getContent().toString()).isEqualTo("some-app-icon");
        assertThat(content.getLocale().getLanguage()).isEqualTo(Locale.forLanguageTag("sv").getLanguage());
    }

    @Test
    public void ensureReadsPropertByteContentFromJSON() throws Exception {

        byte[] bytes = new byte[] { 1, 2, 3 };

        String json = Contents.withMimeType(MimeType.IMAGE_APPICON).andValue(bytes).asJSON().replace("[", "")
                .replace("]", "");

        Content content = new ObjectMapper().readValue(json.getBytes(), Content.class);

        assertThat(content.getMimeType()).isEqualTo(MimeType.IMAGE_APPICON_VAL);
        assertThat((byte[]) content.getContent()).containsExactly(bytes);
    }
}
