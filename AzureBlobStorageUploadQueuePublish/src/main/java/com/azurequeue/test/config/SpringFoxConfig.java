package com.azurequeue.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
//import static com.google.common.base.Predicates.or;
//import com.google.common.base.Predicate;
//import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {                                    
    
	@Bean
	public Docket postsApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.azurequeue.test.controller")).build();
	}

//	private Predicate<String> postPaths() {
//		return  or(regex("/zipfile/*"), regex("/api/zipfile/*"));
//	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Azure upload test API")
				.description("API is used to upload a zip file to azure blob storage")
				.contact(new Contact("Brijesh", "nill", "brijesh.patil@datarynx.com")).license("Temp License")
				.licenseUrl("brijesh.patil@datarynx.com").version("1.0").build();
	}
}
