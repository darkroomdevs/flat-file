package com.github.darkroomdevs.flatfile;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenericTypeTest {

    private GenericType<List<Object>> genericType;

    @BeforeEach
    public void setup() {
        genericType = new GenericType<List<Object>>() {
            /* EMPTY CLASS */
        };
    }

    @Test
    public void typeTest() {
        assertThat(genericType.getType()).isNotNull();
    }

    @Test
    public void classTest() {
        assertThat(genericType.getClazz()).isNotNull();
    }

    @Test
    public void failTest() {
        assertThatThrownBy(() -> {
            //noinspection rawtypes
            new GenericType() {
                /* EMPTY CLASS */
            };
        }).isInstanceOf(java.lang.IllegalArgumentException.class)
                .hasMessage("Internal error: TypeReference constructed without actual type information");
    }
}
