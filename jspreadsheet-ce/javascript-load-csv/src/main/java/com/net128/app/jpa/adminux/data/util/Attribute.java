package com.net128.app.jpa.adminux.data.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class Attribute {
	String name;
	AttributeType type;
	Set<String> enumConstants;

	Attribute(javax.persistence.metamodel.Attribute<?, ?> attribute) {
		name = attribute.getName();
		var javaType = attribute.getJavaType();
		if (Number.class.isAssignableFrom(javaType)) {
			if (javaType.getName().toLowerCase()
					.matches(".*(long|integer|short|byte).*"))
				type = AttributeType.Int;
			else type = AttributeType.Float;
		} else if (javaType.isEnum()) {
			type = AttributeType.Enum;
			enumConstants = Arrays.stream(javaType.getEnumConstants())
					.map(Object::toString).collect(Collectors.toSet());
		} else type = AttributeType.String;
		log.info(name + ": " + attribute.getJavaType() + " -> " + type);
	}
}
