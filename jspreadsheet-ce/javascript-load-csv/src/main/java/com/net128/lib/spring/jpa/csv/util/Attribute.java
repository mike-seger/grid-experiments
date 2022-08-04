package com.net128.lib.spring.jpa.csv.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class Attribute {
	String name;
	AttributeType type;
	Set<String> enumConstants;

	boolean hidden;

	Attribute(javax.persistence.metamodel.Attribute<?, ?> attribute) {
		name = attribute.getName();
		var javaType = attribute.getJavaType();
		if (Number.class.isAssignableFrom(javaType) || javaType.isPrimitive()) {
			var typeName = javaType.getName().toLowerCase();
			if(typeName.equals("char"))
				type = AttributeType.String;
			else if (typeName.matches(".*(long|integer|short|byte|int|boolean).*"))
				type = AttributeType.Int;
			else type = AttributeType.Float;
		} else if (javaType.isEnum()) {
			type = AttributeType.Enum;
			enumConstants = Arrays.stream(javaType.getEnumConstants())
				.map(Object::toString).collect(Collectors.toSet());
		} else if (javaType.equals(Instant.class)
				||javaType.equals(ZonedDateTime.class)
				||javaType.equals(OffsetDateTime.class)
			) {
			type = AttributeType.DateTime;
		} else if (javaType.equals(Date.class) || javaType.equals(LocalDate.class)) {
			type = AttributeType.Date;
		} else type = AttributeType.String;
		hidden = Props.isHiddenField(attribute.getJavaMember().getDeclaringClass(), name);
		log.info(name + ": " + attribute.getJavaType() + " -> " + type);
	}
}
