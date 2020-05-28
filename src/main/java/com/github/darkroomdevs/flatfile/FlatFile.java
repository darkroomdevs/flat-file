package com.github.darkroomdevs.flatfile;

import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public final class FlatFile {

    private static final String SET_METHOD_PREFIX = "set";

    private final Map<String, Object> values;

    private FlatFile(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> asMap() {
        return values;
    }

    @SneakyThrows
    public <T> T asObject(Class<T> clazz) {
        final T object = clazz.getConstructor().newInstance();
        values.forEach((key, value) -> {
            try {
                clazz.getDeclaredMethod(
                        SET_METHOD_PREFIX.concat(StringUtils.capitalize(key)), value.getClass())
                        .invoke(object, value);
            } catch (Exception e) {
                /* Do nothing! */
            }
        });
        return object;
    }

    public static FlatFileParser<String> parser(String row) {
        return new FlatFileParserBuilder<>(row);
    }

    public interface FlatFileParser<T> {

        FlatFileField<T> field(String name);

        FlatFileField<T> alias(String name);

        FlatFile build();
    }

    public interface FlatFileField<T> {

        FlatFileField<T> length(int length);

        FlatFileField<T> withoutPad();

        FlatFileField<T> pad(char padChar);

        FlatFileField<T> leftPad(char padChar);

        FlatFileField<T> rightPad(char padChar);

        <U> FlatFileField<T> type(Class<U> clazz);

        <U> FlatFileField<T> withConverter(FlatFileFieldConverter<U> converter);

        FlatFileParser<T> add();
    }

    public interface FlatFileFieldConverter<T> {

        T toObject(String value);
    }

    private static class FlatFileFieldBuilder<T> implements FlatFileField<T> {

        private final String name;
        private final boolean freeze;
        private final Map<String, Object> values;
        private final FlatFileParserBuilder<T> flatFileParser;

        private int length;
        private Class<?> clazz;
        private FlatFileFieldConverter<?> converter;

        private String padChar;
        private String padCharFromLeft;
        private String padCharFromRight;

        public FlatFileFieldBuilder(String name, boolean freeze,
                final Map<String, Object> values, final FlatFileParser<T> flatFileParser) {
            this.name = name;
            this.freeze = freeze;
            this.values = values;
            this.flatFileParser = (FlatFileParserBuilder<T>) flatFileParser;
            this.length = 1;
            this.clazz = String.class;
            this.padChar = StringUtils.SPACE;
        }

        @Override
        public FlatFileField<T> length(int length) {
            this.length = length;
            return this;
        }

        @Override
        public FlatFileField<T> pad(char padChar) {
            this.padChar = Character.toString(padChar);
            return this;
        }

        @Override
        public FlatFileField<T> withoutPad() {
            this.padChar = null;
            return this;
        }

        @Override
        public FlatFileField<T> leftPad(char padChar) {
            this.padCharFromLeft = Character.toString(padChar);
            return this;
        }

        @Override
        public FlatFileField<T> rightPad(char padChar) {
            this.padCharFromRight = Character.toString(padChar);
            return this;
        }

        @Override
        public <U> FlatFileField<T> type(Class<U> clazz) {
            this.clazz = clazz;
            return this;
        }

        @Override
        public <U> FlatFileField<T> withConverter(FlatFileFieldConverter<U> converter) {
            this.converter = converter;
            return this;
        }

        @Override
        public FlatFileParser<T> add() {
            values.put(name, extract(length, clazz));
            flatFileParser.forwardCursor(freeze ? 0 : length);
            return flatFileParser;
        }

        @SneakyThrows
        private <U> U extract(int length, Class<U> clazz) {
            String value = strip(
                    StringUtils.substring(
                            flatFileParser.row,
                            flatFileParser.cursor,
                            flatFileParser.cursor + length));
            if (converter != null) return clazz.cast(converter.toObject(value));
            return clazz.getConstructor(String.class).newInstance(value);
        }

        private String strip(String value) {
            String processing = StringUtils.defaultIfBlank(value, null);
            if (padCharFromLeft == null && padCharFromRight == null && padChar != null) {
                processing = StringUtils.strip(processing, padChar);
            } else {
                if (padCharFromLeft != null) processing = StringUtils.stripStart(processing, padCharFromLeft);
                if (padCharFromRight != null) processing = StringUtils.stripEnd(processing, padCharFromRight);
            }
            return processing;
        }
    }

    private static class FlatFileParserBuilder<T> implements FlatFileParser<T> {

        private final Map<String, Object> values;
        private final String row;

        private int cursor;

        public FlatFileParserBuilder(T row) {
            this.row = row.toString();
            this.values = new HashMap<>();
            this.cursor = 0;
        }

        private void forwardCursor(int length) {
            this.cursor += length;
        }

        @Override
        public FlatFileField<T> field(String name) {
            return new FlatFileFieldBuilder<>(name, false, values, this);
        }

        @Override
        public FlatFileField<T> alias(String name) {
            return new FlatFileFieldBuilder<>(name, true, values, this);
        }

        @Override
        public FlatFile build() {
            return new FlatFile(this.values);
        }
    }
}
