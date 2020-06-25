package com.example.wenda.Configuration;

import com.example.wenda.Interceptor.LoginRequredInterceptor;
import com.example.wenda.Interceptor.PassportInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//注册拦截器，加入拦截器链路中
@Component
public class WendaWebConfiguration  extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;
   @Autowired
    LoginRequredInterceptor loginRequredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor( loginRequredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
