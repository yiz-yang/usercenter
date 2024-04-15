package com.rong.usercenterbackend.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
//@Profile({"dev","test"})
public class SwaggerConfig {

    @Bean
    public Docket createRestApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //这里一定要标注你控制器的位置  com.rong.usercenterbackend.controller
                .apis(RequestHandlerSelectors.basePackage("com.rong.usercenterbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户中心")
                .description("用户中心接口文档")
                .contact(new Contact("小羊", "www.baidu.com", "xxx@qq.com"))
                .version("1.0")
                .build();
    }
}
