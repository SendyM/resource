package com.meilitech.zhongyi.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swaggers {

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Sendy","daijkl@qq.com","");

        return new ApiInfoBuilder().title("数据源接口文档")
                .description("数据源API相关接口文档")
                .version("1.0.0").contact(contact)
                .build();
    }
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.meilitech.zhongyi.resource.controller.monitor"))
                .paths( PathSelectors.any())
                .build();
    }


}