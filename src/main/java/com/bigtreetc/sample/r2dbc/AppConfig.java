package com.bigtreetc.sample.r2dbc;

import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.*;
import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.SWAGGER_DOCS_URL;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntity;
import com.bigtreetc.sample.r2dbc.base.util.MessageUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.converter.IntegerValueEnumConverterFactory;
import com.bigtreetc.sample.r2dbc.base.web.controller.converter.StringValueEnumConverterFactory;
import com.bigtreetc.sample.r2dbc.base.web.filter.ElapsedMillisLoggingFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.PropertyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Spring Boot R2DBC Rest Sample", version = "0.0.1"))
public class AppConfig implements WebFluxConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverterFactory(new StringValueEnumConverterFactory());
    registry.addConverterFactory(new IntegerValueEnumConverterFactory());
  }

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    val resolver = new ReactivePageableHandlerMethodArgumentResolver();
    resolver.setOneIndexedParameters(true);
    configurer.addCustomResolver(resolver);
  }

  @Bean
  public ElapsedMillisLoggingFilter elapsedMillisLoggingFilter() {
    val matcher =
        new NegatedServerWebExchangeMatcher(
            pathMatchers(
                ACTUATOR_URL,
                SWAGGER_RESOURCES_URL,
                SWAGGER_UI_URL,
                SWAGGER_DOCS_URL,
                WEBJARS_URL));
    val filter = new ElapsedMillisLoggingFilter();
    filter.setRequiresAuthenticationMatcher(matcher);
    return filter;
  }

  @Bean
  public ForwardedHeaderTransformer forwardedHeaderTransformer() {
    return new ForwardedHeaderTransformer();
  }

  @Bean
  @Primary
  public LocalValidatorFactoryBean beanValidator(MessageSource messageSource) {
    val bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    return bean;
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor(
      LocalValidatorFactoryBean localValidatorFactoryBean) {
    val bean = new MethodValidationPostProcessor();
    bean.setValidator(localValidatorFactoryBean);
    return bean;
  }

  @Bean
  public ModelMapper modelMapper() {
    val modelMapper = new ModelMapper();
    val configuration = modelMapper.getConfiguration();
    configuration.setPropertyCondition(
        context -> {
          // IDは上書きしないようにする
          PropertyInfo propertyInfo = context.getMapping().getLastDestinationProperty();
          return !(context.getParent().getDestination() instanceof BaseEntity
              && propertyInfo.getName().equals("id"));
        });
    // 厳格にマッピングする
    configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }

  @Bean
  public OpenAPI customizeOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Autowired
  public void initUtils(MessageSource messageSource) {
    MessageUtils.init(messageSource);
  }
}
