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

        FlatFileField<T> field(String name);

        FlatFile build();
    }

    public interface FlatFileField<T> {

        FlatFileField<T> length(int length);

        <U> FlatFileField<T> type(Class<U> clazz);

        FlatFileParser<T> add();
    }

    private static class FlatFileFieldBuilder<T> implements FlatFileField<T> {

        private final String name;
        private final Map<String, Object> values;
        private final FlatFileParserBuilder<T> flatFileParser;

        private int length;
        private Class<?> clazz;

        public FlatFileFieldBuilder(String name, final Map<String, Object> values, final FlatFileParser<T> flatFileParser) {
            this.name = name;
            this.values = values;
            this.flatFileParser = (FlatFileParserBuilder<T>) flatFileParser;
            this.length = 1;
            this.clazz = String.class;
        }

        @Override
        public FlatFileField<T> length(int length) {
            this.length = length;
            return this;
        }

        @Override
        public <U> FlatFileField<T> type(Class<U> clazz) {
            this.clazz = clazz;
            return this;
        }

        @Override
        public FlatFileParser<T> add() {
            values.put(name, extract(length, clazz));
            flatFileParser.forwardCursor(length);
            return flatFileParser;
        }

        @SneakyThrows
        private <V> V extract(int length, Class<V> clazz) {
            return clazz.getConstructor(String.class)
                    .newInstance(StringUtils.trimToNull(
                            StringUtils.substring(
                                    flatFileParser.row,
                                    flatFileParser.cursor,
                                    flatFileParser.cursor + length)));
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

        void forwardCursor(int length) {
            this.cursor += length;
        }

        @Override
        public FlatFileField<T> field(String name) {
            return new FlatFileFieldBuilder<>(name, values, this);
        }

        @Override
        public FlatFile build() {
            return new FlatFile(this.values);
        }
    }
}
