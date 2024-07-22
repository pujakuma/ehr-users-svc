package com.ehr.usersvc.swagger;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
	
	@Value("${swagger.requestHandlerSelectSelectors}")
	private String requestHandlerSelectSelectors;
	
	@Value("${swagger.pathSelectors}")
	private String pathSelectors;
	
	@Value("${swagger.serviceName}")
	private String serviceName;
	
	@Value("${swagger.serviceDescription}")
	private String serviceDescription;
	
	@Value("${swagger.serviceVersion}")
	private String serviceVersion;
	
	@Value("${swagger.contactGroup}")
	private String contactGroup;
	
	@Value("${swagger.contactGroupEmail}")
	private String contactGroupEmail;
	
	//private static final String DEFAULT_URL ="http://localhost:8080/service/user/api/test/all";
	private static final String DEFAULT_URL ="http://localhost:8081/all";
	@Bean
	public Docket api(){
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage(requestHandlerSelectSelectors))
				.paths(PathSelectors.ant(pathSelectors)).build().apiInfo(apiInfo());
		
	}
	private ApiInfo apiInfo(){
		return new ApiInfo(
				serviceName,
				serviceDescription,
				serviceVersion,
				SwaggerConfig.DEFAULT_URL,
				new Contact(contactGroup,SwaggerConfig.DEFAULT_URL,
						contactGroupEmail),
				"License",
				SwaggerConfig.DEFAULT_URL,
				/*"Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",*/
				Collections.emptyList());
	}
	
	
}
