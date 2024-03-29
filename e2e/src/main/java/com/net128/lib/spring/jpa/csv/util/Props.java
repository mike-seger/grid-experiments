package com.net128.lib.spring.jpa.csv.util;

import java.lang.annotation.Inherited;
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

    @Target({ ANNOTATION_TYPE, TYPE_USE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface Sortable{}

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReadOnly{}

    public static boolean isAnnotatedClass(Class<?> clazz, Class<?> annotationClass) {
        try {
            return Arrays.stream(clazz.getDeclaredAnnotations())
                .anyMatch(x -> Objects.equals(
                    x.annotationType().getCanonicalName(),
                    annotationClass.getCanonicalName()));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAnnotatedField(Class<?> clazz, Class<?> annotationClass, String field) {
        try {
            return Arrays.stream(clazz.getDeclaredField(field).getDeclaredAnnotations())
                .anyMatch(x -> Objects.equals(
                    x.annotationType().getCanonicalName(),
                    annotationClass.getCanonicalName()));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSortable(Class<?> clazz) {
        return isAnnotatedClass(clazz, Sortable.class);
    }

    public static boolean isReadOnlyField(Class<?> clazz, String field) {
        return isAnnotatedField(clazz, ReadOnly.class, field);
    }

    public static boolean isHiddenField(Class<?> clazz, String field) {
        return isAnnotatedField(clazz, Hidden.class, field);
    }
}
