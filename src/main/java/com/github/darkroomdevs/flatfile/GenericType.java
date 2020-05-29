package com.github.darkroomdevs.flatfile;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;

public abstract class GenericType<T> {

    @Getter protected final Type type;
    @Getter protected final Class<T> clazz;

    protected GenericType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            //noinspection unchecked
            clazz = (Class<T>) ((ParameterizedType) type).getRawType();
        }
    }
}
