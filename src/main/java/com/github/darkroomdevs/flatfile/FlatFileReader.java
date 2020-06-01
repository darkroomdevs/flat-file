package com.github.darkroomdevs.flatfile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import lombok.SneakyThrows;

public final class FlatFileReader {

    private final File file;
    private boolean ignoreFirst;
    private boolean ignoreLast;

    private FlatFileReader(File file) {
        this.file = file;
        this.ignoreFirst = false;
        this.ignoreLast = false;
    }

    public static FlatFileReader read(File file) {
        return new FlatFileReader(file);
    }

    public FlatFileReader ignoreFirst() {
        this.ignoreFirst = true;
        return this;
    }

    public FlatFileReader ignoreLast() {
        this.ignoreLast = true;
        return this;
    }

    public FlatFile.FlatFileParser<String> asStringParser() {
        return FlatFile.parser(loadFile());
    }

    @SneakyThrows
    private List<String> loadFile() {
        List<String> rows = Files.readAllLines(file.toPath());
        if (ignoreFirst) rows.remove(0);
        if (ignoreLast) rows.remove(rows.size() - 1);
        return rows;
    }
}
