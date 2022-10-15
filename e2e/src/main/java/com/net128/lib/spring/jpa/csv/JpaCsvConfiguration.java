package com.net128.lib.spring.jpa.csv;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "com.net128.lib.spring.jpa.csv")
@Data
public class JpaCsvConfiguration {
	private Map<String, List<String>> attributeOrders;
}
