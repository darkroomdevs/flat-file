package com.github.darkroomdevs.flatfile;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlatFileTest {

    private static final String ROW_UNDER_TEST = "AYRTON SENNA   1 Lotus Team     ";

    private String row;

    @BeforeEach
    public void setup() {
        row = ROW_UNDER_TEST;
    }

    @Test
    public void assertThatParserExtractSuccessful() {
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
    public void assertThatParserFailWithoutConverter() {
        assertThatThrownBy(() -> {
            // @formatter:off
            //noinspection ResultOfMethodCallIgnored
            FlatFile.parser(row)
                .field("initial")
                    .type(Character.class)
                .add()
            .build()
                .asMap();
            // @formatter:on
        }).isInstanceOf(java.lang.NoSuchMethodException.class);
    }
}
