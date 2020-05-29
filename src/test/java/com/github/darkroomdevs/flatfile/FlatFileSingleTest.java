package com.github.darkroomdevs.flatfile;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlatFileSingleTest {

    private static final String ROW_UNDER_TEST = "AYRTON SENNA   1 Lotus Team     ";
    private static final String SPECIAL_PADDED_ROW_UNDER_TEST = "+AYRTON SENNA--X1_Lotus Team____";

    private String row;

    @BeforeEach
    public void setup() {
        row = ROW_UNDER_TEST;
    }

    @Test
    public void assertThatParserExtractAsMap() {
        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(row)
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

        assertThat(map).hasSize(3);
        assertThat(map).extractingByKey("name").isEqualTo("AYRTON SENNA");
        assertThat(map).extractingByKey("number").isEqualTo(1);
        assertThat(map).extractingByKey("team").isEqualTo("Lotus Team");
    }

    @Test
    public void assertThatParserExtractPaddedAsMap() {
        row = SPECIAL_PADDED_ROW_UNDER_TEST;

        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(row)
                    .field("name")
                        .length(15)
                        .leftPad('+')
                        .rightPad('-')
                    .add()
                    .field("number")
                        .length(2)
                        .type(Integer.class)
                        .leftPad('X')
                    .add()
                        .field("team")
                        .length(15)
                        .pad('_')
                    .add()
                .build()
                    .asMap();
        // @formatter:on

        assertThat(map).hasSize(3);
        assertThat(map).extractingByKey("name").isEqualTo("AYRTON SENNA");
        assertThat(map).extractingByKey("number").isEqualTo(1);
        assertThat(map).extractingByKey("team").isEqualTo("Lotus Team");
    }

    @Test
    public void assertThatParserExtractWithoutStripAsMap() {
        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(row)
                    .field("name")
                        .length(15)
                        .withoutPad()
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

        assertThat(map).hasSize(3);
        assertThat(map).extractingByKey("name").isEqualTo("AYRTON SENNA   ");
        assertThat(map).extractingByKey("number").isEqualTo(1);
        assertThat(map).extractingByKey("team").isEqualTo("Lotus Team");
    }

    @Test
    public void assertThatParserExtractWithoutFowardAsMap() {
        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(row)
                    .alias("initial")
                        .length(1)
                    .add()
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

        assertThat(map).hasSize(4);
        assertThat(map).extractingByKey("initial").isEqualTo("A");
        assertThat(map).extractingByKey("name").isEqualTo("AYRTON SENNA");
        assertThat(map).extractingByKey("number").isEqualTo(1);
        assertThat(map).extractingByKey("team").isEqualTo("Lotus Team");
    }

    @Test
    public void assertThatParserExtractAsObject() {
        // @formatter:off
        DataExample dataExample =
                FlatFile.parser(row)
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

        assertThat(dataExample).isNotNull();
        assertThat(dataExample).hasFieldOrPropertyWithValue("name", "AYRTON SENNA");
        assertThat(dataExample).hasFieldOrPropertyWithValue("number", 1);
        assertThat(dataExample).hasFieldOrPropertyWithValue("country", null);
    }

    @Test
    public void assertThatParserExtractWithConverter() {
        // @formatter:off
        Map<String, Object> map =
                FlatFile.parser(row)
                    .field("initial")
                        .length(1)
                        .type(Character.class)
                        .withConverter(value -> value.charAt(0))
                    .add()
                .build()
                    .asMap();
        // @formatter:on

        assertThat(map).hasSize(1);
        assertThat(map).extractingByKey("initial").isEqualTo('A');
    }

    @Test
    public void assertThatParserFailWithoutConverter() {
        assertThatThrownBy(() -> {
            // @formatter:off
            FlatFile.parser(row)
                .field("initial")
                    .type(Character.class)
                .add()
            .build()
                .asMap();
            // @formatter:on
        }).isInstanceOf(java.lang.NoSuchMethodException.class);
    }

    @Test
    public void assertThatReturnFailWithoutConstructor() {
        assertThatThrownBy(() -> {
            // @formatter:off
            FlatFile.parser(row)
                .field("description")
                    .length(15)
                .add()
            .build()
                .asObject(ErrorExample.class);
            // @formatter:on
        }).isInstanceOf(java.lang.NoSuchMethodException.class);
    }

    @Data
    private static class DataExample implements Serializable {

        private String name;
        private Integer number;
        private String country;
    }

    @Data
    private static class ErrorExample implements Serializable {

        private final String description;
    }
}
