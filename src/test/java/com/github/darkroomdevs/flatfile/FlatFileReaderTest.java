package com.github.darkroomdevs.flatfile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlatFileReaderTest {

    private File fileUnderTest;

    @BeforeEach
    public void setup() {
        fileUnderTest = new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("f1.txt")).getFile());
    }

    @Test
    public void assertThatParserExtractAsList() {
        // @formatter:off
        List<Map<String, Object>> list =
                FlatFileReader.read(fileUnderTest)
                .asStringParser()
                    .field("name")
                        .length(15)
                    .add()
                    .field("number")
                        .length(2)
                        .type(Integer.class)
                    .add()
                    .field("team")
                        .length(15)
                    .add()
                .build()
                    .asList();
        // @formatter:on

        assertThat(list).hasSize(2);
        assertThat(list).extracting("name").containsOnly("AYRTON SENNA", "NELSON PIQUET");
        assertThat(list).extracting("number").containsOnly(1, 2);
        assertThat(list).extracting("team").containsOnly("Lotus Team", "McLaren");
    }

    @Test
    public void assertThatParserIgnoreHeader() {
        // @formatter:off
        List<Map<String, Object>> list =
                FlatFileReader.read(fileUnderTest)
                    .ignoreFirst()
                .asStringParser()
                    .field("name")
                        .length(15)
                    .add()
                    .field("number")
                        .length(2)
                        .type(Integer.class)
                    .add()
                    .field("team")
                        .length(15)
                    .add()
                .build()
                    .asList();
        // @formatter:on

        assertThat(list).hasSize(1);
        assertThat(list).extracting("name").containsOnly("NELSON PIQUET");
        assertThat(list).extracting("number").containsOnly(2);
        assertThat(list).extracting("team").containsOnly("McLaren");
    }

    @Test
    public void assertThatParserIgnoreFooter() {
        // @formatter:off
        List<Map<String, Object>> list =
                FlatFileReader.read(fileUnderTest)
                    .ignoreLast()
                .asStringParser()
                    .field("name")
                        .length(15)
                    .add()
                    .field("number")
                        .length(2)
                        .type(Integer.class)
                    .add()
                    .field("team")
                        .length(15)
                    .add()
                .build()
                    .asList();
        // @formatter:on

        assertThat(list).hasSize(1);
        assertThat(list).extracting("name").containsOnly("AYRTON SENNA");
        assertThat(list).extracting("number").containsOnly(1);
        assertThat(list).extracting("team").containsOnly("Lotus Team");
    }

    @Test
    public void assertThatParserIgnoreHeaderAndFooter() {
        // @formatter:off
        List<Map<String, Object>> list =
                FlatFileReader.read(fileUnderTest)
                    .ignoreFirst()
                    .ignoreLast()
                .asStringParser()
                    .field("name")
                        .length(15)
                    .add()
                    .field("number")
                        .length(2)
                        .type(Integer.class)
                    .add()
                    .field("team")
                        .length(15)
                    .add()
                .build()
                    .asList();
        // @formatter:on

        assertThat(list).isEmpty();
    }

    @Test
    public void assertThatReaderFailWhenFileNotExist() {
        assertThatThrownBy(() -> FlatFileReader.read(new File("test.txt")).asStringParser())
                .isInstanceOf(java.nio.file.NoSuchFileException.class);
    }
}
