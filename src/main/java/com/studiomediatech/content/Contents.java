package com.studiomediatech.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Chaining builder for common content, providing an easy to use and developer-friendly API for working with content
 * creation.
 *
 * @since 0.1
 */
public final class Contents {

    private final List<Content> values = new ArrayList<>();

    private Contents() {
        // Hidden
    }

    /**
     * Creates a new contents instance with the given values.
     *
     * @param values
     *            to append to the new contents instance
     */
    public Contents(List<Content> values) {

        this.values.addAll(Optional.ofNullable(values).orElse(Collections.emptyList()));
    }

    /**
     * Creates a new contents builder, starting it of with the given mime-type.
     *
     * @param mimeType
     *            to start building with
     *
     * @return a new contents builder instance
     */
    public static Builder withMimeType(MimeType mimeType) {

        return new ContentBuilder(mimeType, new Contents());
    }

    /**
     * Retrieves the content matching the given MIME-type.
     *
     * @param mimeType
     *            predicate to match
     * @param <T>
     *            inferring the type of content to retrieve
     *
     * @return the content value found, or {@code null} if not content could be found
     *
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public <T> T forMimeType(MimeType mimeType) {

        return (T) this.values.stream().filter(c -> c.forMimeType(mimeType)).findFirst().map(Content::getContent)
                .orElse(null);
    }

    /**
     * Retrieves the content matching the given MIME-type and locale.
     *
     * @param mimeType
     *            predicate to match
     * @param locale
     *            predicate to match
     * @param <T>
     *            inferring the type of content to retrieve
     *
     * @return the content value found, or {@code null} if not content could be found
     *
     * @since 0.2
     */
    @SuppressWarnings("unchecked")
    public <T> T forMimeTypeAndLocale(MimeType mimeType, Locale locale) {

        return (T) this.values.stream().filter(c -> c.forMimeTypeAndLocale(mimeType, locale)).findFirst()
                .map(Content::getContent).orElse(null);
    }

    /**
     * Builder can add value, with an optional locale.
     *
     *
     *
     * @since 0.1
     */
    public interface Builder {

        /**
         * Adds a value to the builder.
         *
         * @param value
         *            to add
         *
         * @return a buildable builder
         */
        Buildable andValue(String value);

        /**
         * Adds a value to the builder with a given locale.
         *
         * @param value
         *            to add
         * @param locale
         *            of the value localization/language
         *
         * @return a buildable builder
         */
        Buildable andValue(String value, Locale locale);

        /**
         * Add a value to the builder for the current default locale.
         *
         * @param value
         *            to add
         *
         * @return a buildable builder
         */
        Buildable andValue(byte[] value);
    }

    /**
     * Appendable can append another mime-type content entry to the builder.
     *
     *
     *
     * @since 0.1
     */
    public interface Appendable extends Builder {

        /**
         * Appends the given mime type to the builder.
         *
         * @param mimeType
         *            to append
         *
         * @return a builder
         */
        Builder andWithMimeType(MimeType mimeType);
    }

    /**
     * Buildable can build the resulting contents.
     *
     *
     *
     * @since 0.1
     */
    public interface Buildable extends Appendable {

        /**
         * Builds the resulting contents as a list of strongly typed content entries.
         *
         * @return a list of content entries
         */
        List<Content> asList();

        /**
         * Builds the resulting content as a collection of maps, reducing avoiding the content type to be bound to the
         * instance.
         *
         * @return a list of map entries
         */
        List<Map<String, Object>> asMap();

        /**
         * Builds the resulting content as a JSON string.
         *
         * @return a JSON string
         */
        String asJSON();
    }

    private static final class ContentBuilder implements Buildable {

        private final Contents contents;
        private final MimeType mimeType;

        private ContentBuilder(MimeType mimeType, Contents contents) {

            this.mimeType = mimeType;
            this.contents = contents;
        }

        private ContentBuilder(ContentBuilder contentBuilder, String value) {

            this.mimeType = contentBuilder.mimeType;
            this.contents = contentBuilder.contents;

            Content content = new Content(this.mimeType.getMimeType(), value);
            this.contents.values.add(content);
        }

        public ContentBuilder(ContentBuilder contentBuilder, byte[] value) {

            this.mimeType = contentBuilder.mimeType;
            this.contents = contentBuilder.contents;

            Content content = new Content(this.mimeType.getMimeType(), value);
            this.contents.values.add(content);
        }

        private ContentBuilder(ContentBuilder contentBuilder, String value, Locale locale) {

            this.mimeType = contentBuilder.mimeType;
            this.contents = contentBuilder.contents;

            Content content = new Content(this.mimeType.getMimeType(), value, locale);
            this.contents.values.add(content);
        }

        @Override
        public Buildable andValue(String value) {

            return isNullOrEmpty(value) ? this : new ContentBuilder(this, value);
        }

        private boolean isNullOrEmpty(String value) {

            return value == null || value.trim().isEmpty();
        }

        @Override
        public Buildable andValue(byte[] value) {

            return isNullOrSizeZero(value) ? this : new ContentBuilder(this, value);
        }

        private boolean isNullOrSizeZero(byte[] value) {

            return value == null || value.length == 0;
        }

        @Override
        public Buildable andValue(String value, Locale locale) {

            return isNullOrEmpty(value) ? this : new ContentBuilder(this, value, locale);
        }

        @Override
        public Builder andWithMimeType(MimeType mimeType) {

            return new ContentBuilder(mimeType, contents);
        }

        @Override
        public List<Content> asList() {

            return Collections.unmodifiableList(new ArrayList<>(this.contents.values));
        }

        @Override
        public List<Map<String, Object>> asMap() {

            return this.contents.values.stream().map(Content::asMap).collect(Collectors.toList());
        }

        @Override
        public String asJSON() {

            try {
                return new ObjectMapper().writeValueAsString(asMap());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not write contents as JSON string", e);
            }
        }
    }
}
