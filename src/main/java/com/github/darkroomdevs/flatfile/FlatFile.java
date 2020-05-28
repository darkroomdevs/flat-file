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
        return new FlatFileParserBuilder<>(row);
    }

    public interface FlatFileParser<T> {

        FlatFileParser<T> field(int length, String name);

        <U> FlatFileParser<T> field(int length, String name, Class<U> clazz);

        FlatFile build();
    }

    private static class FlatFileParserBuilder<T> implements FlatFileParser<T> {

        private final Map<String, Object> values;
        private final String row;

        private int offset;

        public FlatFileParserBuilder(T row) {
            this.row = row.toString();
            this.values = new HashMap<>();
            this.offset = 0;
        }

        @Override
        public FlatFileParserBuilder<T> field(int length, String name) {
            return field(length, name, String.class);
        }

        @Override
        public <U> FlatFileParserBuilder<T> field(int length, String name, Class<U> clazz) {
            values.put(name, extract(length, clazz));
            offset += length;
            return this;
        }

        @Override
        public FlatFile build() {
            return new FlatFile(this.values);
        }

        @SneakyThrows
        private <V> V extract(int length, Class<V> clazz) {
            return clazz.getConstructor(String.class)
                    .newInstance(StringUtils.trimToNull(
                            StringUtils.substring(row, offset, offset + length)));
        }
    }
}
