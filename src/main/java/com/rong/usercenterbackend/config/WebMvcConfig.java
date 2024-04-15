package com.rong.usercenterbackend.config;

import com.rong.usercenterbackend.interceptor.GlobalRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalRequestInterceptor())
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry){
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("http://8.138.135.232:5173","http://localhost:80","http://127.0.0.1:80","http://8.138.135.232:80","http://localhost:4173","http://8.138.135.232","http://userfront.friendsmaker.icu","http://user.friendsmaker.icu")
//                .allowedOriginPatterns("*")
                //是否允许证书，不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600)
                .allowedHeaders("*");

    }

}
