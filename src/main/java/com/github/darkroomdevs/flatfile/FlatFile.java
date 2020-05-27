package com.github.darkroomdevs.flatfile;

import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public final class FlatFile {

    private final Map<String, Object> values;

    private FlatFile(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> asMap() {
        return values;
    }

    public static FlatFileParser<String> parser(String row) {
        return new FlatFileParserString(row);
    }

    public interface FlatFileParser<T> {

        FlatFileParser<T> field(int length, String name);

        <U> FlatFileParser<T> field(int length, String name, Class<U> clazz);

        FlatFile build();
    }

    private static class FlatFileParserString implements FlatFileParser<String> {

        private final Map<String, Object> values;
        private final String row;

        private int offset;

        public FlatFileParserString(String row) {
            this.row = row;
            this.values = new HashMap<>();
            this.offset = 0;
        }

        @Override
        public FlatFileParserString field(int length, String name) {
            return field(length, name, String.class);
        }

        @Override
        public <T> FlatFileParserString field(int length, String name, Class<T> clazz) {
            values.put(name, extract(length, clazz));
            offset += length;
            return this;
        }

        @Override
        public FlatFile build() {
            return new FlatFile(this.values);
        }

        @SneakyThrows
        private <T> T extract(int length, Class<T> clazz) {
            return clazz.getConstructor(String.class)
                    .newInstance(StringUtils.trimToNull(
                            StringUtils.substring(row, offset, offset + length)));
        }
    }
}
