package com.net128.lib.spring.jpa.csv.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;

public class Props {
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Hidden{}

    public static boolean isHiddenField(Class<?> clazz, String field) {
        try {
            return Arrays.stream(clazz.getDeclaredField(field).getDeclaredAnnotations())
                .anyMatch(x -> Objects.equals(
                    x.annotationType().getCanonicalName(),
                    Hidden.class.getCanonicalName()));
        } catch (Exception e) {
            return false;
        }
    }
}
