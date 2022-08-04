package com.net128.lib.spring.jpa.csv.util;

import com.net128.lib.spring.jpa.csv.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class JpaMapper {
	private final EntityManager entityManager;
	private final Map<String, Class<?>> entityClassMap;
	private final Map<Class<?>, JpaRepository<?, Long>> entityRepoMap;

	public JpaMapper(EntityManager entityManager, Set<JpaRepository<?, Long>> jpaRepositories) {
		this.entityManager = entityManager;
		entityClassMap = getEntityClassMap();
		entityRepoMap =	jpaRepositories.stream().collect(Collectors.toMap(JpaMapper::getEntity, j -> j));
	}
	public LinkedHashMap<String, Attribute> getAttributes(String entity) {
		var result = getMetaAttributes(entity).stream().sorted(Comparator.comparing(javax.persistence.metamodel.Attribute::getName)).collect(
			Collectors.toMap(
				javax.persistence.metamodel.Attribute::getName,
				Attribute::new, (v1,v2) -> v1, LinkedHashMap::new));
		if(result.containsKey("id")) {
			var id = result.remove("id");
			var old = result;
			result = new LinkedHashMap<>();
			result.put(id.name, id);
			result.putAll(old);
		}
		return result;
	}

	public List<String> getEntities() {
		return entityClassMap.keySet().stream().sorted().collect(Collectors.toList());
	}

	private Set<javax.persistence.metamodel.Attribute<?,?>> getMetaAttributes(String entity) {
		return getEntityMetaData(getEntityClass(entity));
	}

	public Class<?> getEntityClass(String entityName) {
		var entityClass = entityClassMap.get(entityName);
		if(entityClass == null)
			throw new ValidationException("Unable to find entity class for: "+entityName);
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	private Set	<javax.persistence.metamodel.Attribute<?, ?>> getEntityMetaData(Class<?> entityClass) {
		return (Set<javax.persistence.metamodel.Attribute<?, ?>>) entityManager.getMetamodel().entity(entityClass).getAttributes();
	}

	private Map<String, Class<?>> getEntityClassMap() {
		return entityManager.getMetamodel().getEntities()
				.stream().collect(Collectors.toMap(
						e -> camel2Snake(e.getName()), javax.persistence.metamodel.Type::getJavaType));
	}

	public JpaRepository<?, Long> getEntityRepository(Class<?> entityClass) {
		var repo = entityRepoMap.get(entityClass);
		if(repo == null)
			throw new ValidationException("Unable to get repository for entity class: "+entityClass.getSimpleName());
		return repo;
	}

	private String camel2Snake(String str) {
		return str.replaceAll("([A-Z][a-z])", "_$1")
				.replaceAll("^_", "").toUpperCase();
	}

	@SuppressWarnings("rawtypes")
	private static Class<?> getEntity(JpaRepository repo) {
		var clazzes = getGenericType(repo.getClass())[0];
		var jpaClass = getGenericType(getClassFromType(clazzes));
		return getClassFromType( ((ParameterizedType)jpaClass[0]).getActualTypeArguments()[0]);
	}

	private static Type[] getGenericType(Class<?> target) {
		if (target == null) return new Type[0];
		var types = target.getGenericInterfaces();
		if (types.length > 0) return types;
		var type = target.getGenericSuperclass();
		if (type instanceof ParameterizedType) return new Type[] { type };
		return new Type[0];
	}

	@SuppressWarnings("rawtypes")
	private static Class<?> getClassFromType(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClassFromType(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			var componentType = ((GenericArrayType) type).getGenericComponentType();
			var componentClass = getClassFromType(componentType);
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
