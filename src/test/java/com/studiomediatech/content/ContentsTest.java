package com.studiomediatech.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.studiomediatech.content.Contents.Buildable;
import com.studiomediatech.content.Contents.Builder;

public class ContentsTest {

    @Test
    public void ensureContentsBuiltWithSingleSubjectNoLocalye() {

        Builder b1 = Contents.withMimeType(MimeType.TEXT_SUBJECT);
        assertThat(b1).isNotNull();

        Buildable b2 = b1.andValue("foobar");
        assertThat(b2).isNotNull();
        assertThat(b2).isNotSameAs(b1);

        List<Content> contents = b2.asList();

        assertThat(contents).isNotEmpty().hasSize(1);

        assertContentEquals(contents.get(0), MimeType.TEXT_SUBJECT, "foobar", null);
    }

    private void assertContentEquals(Content content1, MimeType type, String content, Locale locale) {

        assertThat(content1.getMimeType()).isEqualTo(type.getMimeType());
        assertThat(content1.getContent()).isEqualTo(content);
        assertThat(content1.getLocale()).isEqualTo(locale);
    }

    @Test
    public void ensureContentBuiltWithLocalizedDescription() throws Exception {

        List<? extends Content> contents = Contents.withMimeType(MimeType.TEXT_DESCRIPTION)
                .andValue("Hello", Locale.ENGLISH).asList();

        assertThat(contents).isNotNull().isNotEmpty().hasSize(1);

        assertContentEquals(contents.get(0), MimeType.TEXT_DESCRIPTION, "Hello", Locale.ENGLISH);
    }

    @Test
    public void ensureTwoValuesForOneMimeType() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_BODY).andValue("Say it")
                .andValue("Säg det", Locale.forLanguageTag("sv")).asList();

        assertThat(contents).isNotNull().hasSize(2);

        assertContentEquals(contents.get(0), MimeType.TEXT_BODY, "Say it", null);
        assertContentEquals(contents.get(1), MimeType.TEXT_BODY, "Säg det", Locale.forLanguageTag("sv"));
    }

    @Test
    public void ensureMultipleMimeTypes() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_SUBJECT).andValue("Introducing, the magnificent")
                .andWithMimeType(MimeType.TEXT_BODY).andValue("Welcome to the first day of the rest of your life!")
                .andWithMimeType(MimeType.TEXT_DESCRIPTION).andValue("Message").andValue("Nachricht", Locale.GERMAN)
                .asList();

        assertThat(contents).isNotNull().hasSize(4);

        assertContentEquals(contents.get(0), MimeType.TEXT_SUBJECT, "Introducing, the magnificent", null);
        assertContentEquals(contents.get(1), MimeType.TEXT_BODY, "Welcome to the first day of the rest of your life!",
                null);
        assertContentEquals(contents.get(2), MimeType.TEXT_DESCRIPTION, "Message", null);
        assertContentEquals(contents.get(3), MimeType.TEXT_DESCRIPTION, "Nachricht", Locale.GERMAN);
    }

    @Test
    public void ensureBuildsAsProperMap() throws Exception {

        List<Map<String, Object>> contents = Contents.withMimeType(MimeType.TEXT_BODY).andValue("Say it")
                .andValue("Säg det", Locale.forLanguageTag("sv")).asMap();

        assertThat(contents).isNotNull().hasSize(2);

        assertContentEquals(contents.get(0), MimeType.TEXT_BODY, "Say it", null);
        assertContentEquals(contents.get(1), MimeType.TEXT_BODY, "Säg det", Locale.forLanguageTag("sv"));
    }

    private void assertContentEquals(Map<String, Object> entry, MimeType type, String content, Locale locale) {

        assertThat(entry).containsKey("mimeType").containsKey("content");
        assertThat(entry.get("mimeType")).isEqualTo(type.getMimeType());
        assertThat(entry.get("content")).isEqualTo(content);
        assertThat(entry.get("locale")).isEqualTo(Optional.ofNullable(locale).map(Locale::toString).orElse(null));
    }

    @Test
    public void ensureReturnsJsonString() throws Exception {

        String json = Contents.withMimeType(MimeType.TEXT_BODY).andValue("Say it").andValue("Säg det", Locale.forLanguageTag("sv"))
                .asJSON();

        JSONAssert.assertEquals("[" + "{mimeType: 'text/vnd.content.body', content: 'Say it'}, "
                + "{mimeType: 'text/vnd.content.body', content: 'Säg det', locale: 'sv'}]", json, true);
    }

    @Test
    public void ensureRetrievesContentByMimeType() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_BODY).andValue("Say it")
                .andValue("Säg det", Locale.forLanguageTag("sv")).asList();

        String value = new Contents(contents).forMimeType(MimeType.TEXT_BODY);

        assertThat(value).isEqualTo("Say it");
    }

    @Test
    public void ensureMatchesVariants() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_BODY.withParams("foo")).andValue("variant")
                .andWithMimeType(MimeType.TEXT_BODY).andValue("default").asList();

        String v1 = new Contents(contents).forMimeType(MimeType.TEXT_BODY);
        String v2 = new Contents(contents).forMimeType(MimeType.TEXT_BODY.withParams("foo"));

        assertThat(v1).isEqualTo("default");
        assertThat(v2).isEqualTo("variant");
    }

    @Test
    public void ensureRetreivesContentByMimeTypeAndLocale() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_BODY).andValue("Say it")
                .andValue("Säg det", Locale.forLanguageTag("sv")).asList();

        String value = new Contents(contents).forMimeTypeAndLocale(MimeType.TEXT_BODY, Locale.forLanguageTag("sv"));

        assertThat(value).isEqualTo("Säg det");
    }

    @Test
    public void ensureCanCreateAndRetriveBinaryImageData() throws Exception {

        byte[] image = new byte[] { 1, 2, 3 };
        List<Content> contents = Contents.withMimeType(MimeType.IMAGE_APPICON).andValue(image).asList();

        byte[] value = new Contents(contents).forMimeType(MimeType.IMAGE_APPICON);
        assertThat(value).isEqualTo(image);
    }

    @Test
    public void ensureIgnoresEmptyContentEntries() throws Exception {

        List<Content> contents = Contents.withMimeType(MimeType.TEXT_APPICON).andValue("    ")
                .andWithMimeType(MimeType.TEXT_SUBJECT).andValue("").andWithMimeType(MimeType.TEXT_BODY)
                .andValue((String) null).andWithMimeType(MimeType.TEXT_DESCRIPTION).andValue("foo").asList();

        assertThat(contents).isNotNull().hasSize(1);
        assertContentEquals(contents.get(0), MimeType.TEXT_DESCRIPTION, "foo", null);
    }
}
