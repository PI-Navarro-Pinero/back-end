package com.pi.back.utils;

import org.springframework.util.Assert;

public class Validations {

    public static String notNullNorEmpty(final String value, final String name) {
        Assert.isTrue(!notNull(value, name).isEmpty(), name + " cannot be empty");
        return value;
    }

    public static <T> T notNull(final T t, final String name) {
        Assert.notNull(t, name + " cannot be null");
        return t;
    }
}
