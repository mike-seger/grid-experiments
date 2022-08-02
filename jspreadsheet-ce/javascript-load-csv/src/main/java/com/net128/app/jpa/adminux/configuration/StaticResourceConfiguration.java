package com.net128.app.jpa.adminux.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.setOrder(Ordered.LOWEST_PRECEDENCE)
			.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/", "classpath:/public/")
			.resourceChain(true)
			.addResolver(new IndexFallbackResourceResolver());
	}

	@SuppressWarnings("NullableProblems")
	private static class IndexFallbackResourceResolver extends PathResourceResolver {
		public Resource resolveResourceInternal(HttpServletRequest request,
				String requestPath,
				List<? extends Resource> locations,
				ResourceResolverChain chain) {
			var resource = super.resolveResourceInternal(
				request, requestPath, locations, chain);
			if (resource == null) {
				resource = super.resolveResourceInternal(request,
					requestPath+"index.html", locations, chain);
			}
			return resource;
		}
	}
}
