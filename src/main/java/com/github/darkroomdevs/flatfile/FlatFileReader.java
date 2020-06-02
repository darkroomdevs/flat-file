package com.github.darkroomdevs.flatfile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;

public final class FlatFileReader {

    private final List<String> rows;

    private Decorator header;
    private Decorator footer;

    @SneakyThrows
    private FlatFileReader(File file) {
        this.rows = Files.readAllLines(file.toPath());
    }

    public static FlatFileReader read(File file) {
        return new FlatFileReader(file);
    }

    public Decorator header() {
        if (header == null) header = new FlatFileReaderDecorator(this);
        return header;
    }

    public Decorator footer() {
        if (footer == null) footer = new FlatFileReaderDecorator(this, rows.size() - 1);
        return footer;
    }

    public FlatFile.FlatFileParser<String> asStringParser() {
        return FlatFile.parser(loadFile());
    }

    private void loadDecorator(FlatFileReaderDecorator decorator, int decoratorIndex) {
        if (decorator != null && decorator.isIgnore() && !rows.isEmpty()) rows.remove(decoratorIndex);
    }

    private List<String> loadFile() {
        loadDecorator((FlatFileReaderDecorator) header, 0);
        loadDecorator((FlatFileReaderDecorator) footer, rows.size() - 1);
        return rows;
    }

    public interface Decorator {

        Decorator ignore();

        String raw();

        FlatFileReader asString();
    }

    private static class FlatFileReaderDecorator implements Decorator {

        private final FlatFileReader flatFileReader;
        private final int index;

        private String raw;
        @Getter private boolean ignore;

        private FlatFileReaderDecorator(FlatFileReader flatFileReader) {
            this(flatFileReader, 0);
        }

        private FlatFileReaderDecorator(FlatFileReader flatFileReader, int index) {
            this.flatFileReader = flatFileReader;
            this.index = index;
        }

        @Override
        public Decorator ignore() {
            ignore = true;
            return this;
        }

        @Override
        public String raw() {
            return raw;
        }

        @Override
        public FlatFileReader asString() {
            raw = flatFileReader.rows.get(index);
            return flatFileReader;
        }
    }
}
