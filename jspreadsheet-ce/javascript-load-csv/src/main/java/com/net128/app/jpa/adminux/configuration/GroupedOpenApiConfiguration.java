package com.net128.app.jpa.adminux.configuration;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Configuration
@Slf4j
public class GroupedOpenApiConfiguration {

	public GroupedOpenApiConfiguration(
			ApplicationContext context,
			ConfigurableListableBeanFactory beanFactory,
			@Value("${com.net128.shared.web.extra-package-pat:.*net128.*}")
			String extraPackagePat,
			@Value("${com.net128.shared.web.main-package-pat:-}")
			String mainPackagePat) {
		this.context = context;
		this.beanFactory = beanFactory;
		this.extraPackagePat = extraPackagePat;
		this.mainPackagePat = mainPackagePat;
		mainClass = getMainClass();
	}

	private final ApplicationContext context;
	private final ConfigurableListableBeanFactory beanFactory;
	private final String extraPackagePat;
	private final String mainPackagePat;

	public final String propertyUtilsPrimaryName = "springdoc-swagger-ui.urlsPrimaryName";

	public final String defaultMainGroupName = "main";
	private final Class<?> mainClass;

	private GroupedOpenApi apiGroup(String packageName) {
		log.info("Created GroupedOpenApi:"+packageName);
		return GroupedOpenApi.builder().group(packageName).packagesToScan(packageName).build();
	}

	private void registerExtraGroupedOpenApis(Set<String> packageNames) {
		packageNames.forEach(p -> beanFactory.registerSingleton(
			GroupedOpenApi.class.getSimpleName()+"-"+p, apiGroup((p))));
	}

	private Class<?> getMainClass() {
		var applicationClasses = Arrays.stream(
			Objects.requireNonNull(context).getBeanNamesForAnnotation(SpringBootApplication.class))
			.map(n -> context.getBean(n).getClass()).collect(Collectors.toList());
		if(applicationClasses.size()<1) {
			log.error( "No StringBootApplication annotated class found. Using: " + getClass() );
			return getClass();
		}
		if(applicationClasses.size()>1)
			log.info("Using first StringBootApplication from: "+applicationClasses);
		return applicationClasses.get(0);
	}

	@Bean
	public SwaggerUiConfigProperties swaggerUiConfig(SwaggerUiConfigProperties config) {
		config.setConfigUrl(System.getProperty(propertyUtilsPrimaryName));
		return config;
	}

	@Bean
	public GroupedOpenApi mainApi() {
		var packageNames = packageNames();
		if(packageNames.isEmpty()) {
			log.error("No controller package classes found under: "+mainClass.getPackageName());
			return apiGroup(defaultMainGroupName);
		}

		var mainPackage = mainPackagePat.equals("-")?
			packageNames.iterator().next() : mainPackagePat;
		packageNames.remove(mainPackage);
		registerExtraGroupedOpenApis(packageNames);
		return apiGroup(mainPackage);
	}

	private Set<String> packageNames() {
		var scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter((new AnnotationTypeFilter(RestController.class)));
		scanner.addIncludeFilter((new AnnotationTypeFilter(Controller.class)));
		var extraControllers = scanner.findCandidateComponents("com.net128")
			.stream().map(BeanDefinition::getBeanClassName).filter((it -> it !=null && it.matches(extraPackagePat)))
			.map(c -> c.replaceAll("\\.[^.]*$", "")).collect(Collectors.toList());
		var controllerClasses = new Reflections(mainClass.getPackageName())
			.getTypesAnnotatedWith(RestController.class).stream().map(Class::getPackageName).collect(Collectors.toSet());
		controllerClasses.addAll(extraControllers);
		log.info("Controllers found: "+controllerClasses);
		return controllerClasses;
	}
}
