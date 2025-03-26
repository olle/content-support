package com.studiomediatech.content;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MimeTypeTest {

    @Test
    public void ensureDeclaredContentMimeTypeConstants() {

        assertThat(MimeType.TEXT_SUBJECT.getMimeType()).isEqualTo("text/vnd.content.subject");
        assertThat(MimeType.TEXT_DESCRIPTION.getMimeType()).isEqualTo("text/vnd.content.description");
        assertThat(MimeType.TEXT_BODY.getMimeType()).isEqualTo("text/vnd.content.body");
    }

    @Test
    public void ensureEqualsForInstancesAreCorrect() throws Exception {

        assertThat(MimeType.TEXT_SUBJECT.equals(MimeType.TEXT_SUBJECT)).isTrue();
        assertThat(MimeType.TEXT_DESCRIPTION.equals(MimeType.TEXT_DESCRIPTION)).isTrue();

        assertThat(MimeType.TEXT_BODY.equals(MimeType.TEXT_BODY)).isTrue();
        assertThat(MimeType.TEXT_BODY.equals(MimeType.TEXT_DESCRIPTION)).isFalse();
        assertThat(MimeType.TEXT_BODY.equals(MimeType.TEXT_SUBJECT)).isFalse();
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void ensureEqualsForStringComparisonIsAvailable() throws Exception {

        assertThat(MimeType.TEXT_SUBJECT.equals("text/vnd.content.subject")).isTrue();
        assertThat(MimeType.TEXT_SUBJECT.equals("text/vnd.content.description")).isFalse();

        assertThat(MimeType.TEXT_DESCRIPTION.equals("text/vnd.content.description")).isTrue();
        assertThat(MimeType.TEXT_BODY.equals("text/vnd.content.body")).isTrue();
    }

    @Test
    public void ensureCanCreateMimeTypeVariantsWithParams() throws Exception {

        assertThat(MimeType.TEXT_APPICON.withParams("foobar").getMimeType())
                .isEqualTo("text/vnd.content.appicon;foobar");
    }
}
