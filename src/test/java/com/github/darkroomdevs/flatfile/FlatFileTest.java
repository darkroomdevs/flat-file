package com.github.darkroomdevs.flatfile;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
                        .field(15, "name")
                        .field(2, "number", Integer.class)
                        .field(15, "team")
                    .build()
                        .asMap();
        // @formatter:on

        assertThat(map).hasSize(3);
        assertThat(map).extractingByKey("name").isEqualTo("AYRTON SENNA");
        assertThat(map).extractingByKey("number").isEqualTo(1);
        assertThat(map).extractingByKey("team").isEqualTo("Lotus Team");
    }
}
