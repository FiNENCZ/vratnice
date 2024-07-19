package cz.diamo.share.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.HandlerMethod;

import cz.diamo.share.constants.Constants;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfiguration {

	@Bean
	@Profile("dev")
	public GroupedOpenApi ngOpenApi(OperationCustomizer globalOperationCustomizer) {
		final OpenApiCustomizer customizer = openApi -> {
			openApi.info(new Info().title(Constants.PROJECT_NAME + " - API pro NG").version("v1"));
		};
		String packagesToscan[] = { "cz.diamo.share.controller", "cz.diamo.share.websocket",
				"cz.diamo." + Constants.BASE_PACKAGE + "rest.controller",
				"cz.diamo." + Constants.BASE_PACKAGE + ".websocket" };
		return GroupedOpenApi.builder().group(Constants.SCHEMA + "-ng").packagesToScan(packagesToscan)
				.addOpenApiCustomizer(customizer)
				.addOperationCustomizer(globalOperationCustomizer).build();
	}

	@Bean
	public OpenAPI springOpenAPI(@Value("${server.servlet.context-path}") String contextPath) {
		return new OpenAPI().addServersItem(new Server().url(contextPath));
	}

	@Bean
	public OperationCustomizer globalOperationCustomizer() {
		OperationCustomizer c = new OperationCustomizer() {
			@Override
			public Operation customize(Operation operation, HandlerMethod handlerMethod) {
				Parameter customHeaderVersion = new Parameter().in(ParameterIn.QUERY.toString()).name("lang")
						.schema(new StringSchema()).example("cs").required(false);
				operation.addParametersItem(customHeaderVersion);

				String beanName = handlerMethod.getBeanType().getSimpleName();
				operation.setOperationId(
						String.format("%s%s", handlerMethod.getMethod().getName(), beanName.replace("Controller", "")));

				return operation;
			}
		};
		return c;
	}

	@Bean
	public GroupedOpenApi restOpenApi(OperationCustomizer globalOperationCustomizer) {
		final OpenApiCustomizer customizer = openApi -> {
			openApi.getComponents().addSecuritySchemes("basicAuthScheme",
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"));
			openApi.info(new Info().title("REST API").version("v1"));
		};
		String packagesToscan[] = { "cz.diamo.share.rest.controller", "cz.diamo.vratnice.rest.controller"  };
		return GroupedOpenApi.builder().group("rest-api").packagesToScan(packagesToscan)
				.addOpenApiCustomizer(customizer).addOperationCustomizer(globalOperationCustomizer).build();
	}

}
