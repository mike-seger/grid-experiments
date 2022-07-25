package com.net128.app.jpa.adminux;

import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;


public class JpaUtils {
	@SuppressWarnings("rawtypes")
	public static Class<?> getEntity(JpaRepository repo) {
		var clazzes = getGenericType(repo.getClass())[0];
		var jpaClass = getGenericType(getClass(clazzes));
		return getClass( ((ParameterizedType)jpaClass[0]).getActualTypeArguments()[0]);
	}

	public static Type[] getGenericType(Class<?> target) {
		if (target == null) return new Type[0];
		var types = target.getGenericInterfaces();
		if (types.length > 0) return types;
		var type = target.getGenericSuperclass();
		if (type != null && type instanceof ParameterizedType) return new Type[] { type };
		return new Type[0];
	}

	/*
	 * Get the underlying class for a type, or null if the type is a variable
	 * type.
	 *
	 * @param type
	 * @return the underlying class
	 */
	@SuppressWarnings("rawtypes")
	private static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			var componentType = ((GenericArrayType) type).getGenericComponentType();
			var componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
