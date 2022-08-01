package com.net128.app.jpa.adminux.data.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

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
