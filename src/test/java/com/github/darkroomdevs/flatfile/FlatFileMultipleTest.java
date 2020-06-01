package com.github.darkroomdevs.flatfile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlatFileMultipleTest {

    private static final String ROW_UNDER_TEST_1 = "AYRTON SENNA   1 Lotus Team     ";
    private static final String ROW_UNDER_TEST_2 = "NELSON PIQUET  2 McLaren        ";

    private List<String> rows;

    @BeforeEach
    public void setup() {
        rows = Arrays.asList(ROW_UNDER_TEST_1, ROW_UNDER_TEST_2);
    }

    @Test
    public void assertThatParserExtractAsMapReturnsNull() {
        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(rows)
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
                    .asMap();
        // @formatter:on

        assertThat(map).isNull();
    }

    @Test
    public void assertThatParserExtractAsObjectReturnsNull() {
        // @formatter:off
        DataExample dataExample =
                FlatFile.parser(rows)
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
                    .asObject(DataExample.class);
        // @formatter:on

        assertThat(dataExample).isNull();
    }

    @Test
    public void assertThatParserExtractAsList() {
        // @formatter:off
        List<Map<String, Object>> list =
                FlatFile.parser(rows)
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
    public void assertThatParserExtractAsObjectList() {
        // @formatter:off
        List<DataExample> list =
                FlatFile.parser(rows)
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
                    .asObjectList(DataExample.class);
        // @formatter:on

        assertThat(list).hasSize(2);
        assertThat(list).extracting("name").containsOnly("AYRTON SENNA", "NELSON PIQUET");
        assertThat(list).extracting("number").containsOnly(1, 2);
        assertThat(list).extracting("team").containsOnly("Lotus Team", "McLaren");
    }

    @Data
    private static class DataExample implements Serializable {

        private String name;
        private Integer number;
        private String team;
    }
}
