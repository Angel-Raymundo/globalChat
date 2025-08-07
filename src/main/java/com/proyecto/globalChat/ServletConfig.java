package com.proyecto.globalChat;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class ServletConfig {

	@Bean
	public ServletRegistrationBean<FormServlet> registrarFormServlet() {
	    ServletRegistrationBean<FormServlet> bean = new ServletRegistrationBean<>(new FormServlet(), "/procesar", "/mensajes");
	    bean.setLoadOnStartup(1);
	    
	    bean.setMultipartConfig(new MultipartConfigElement(""));
	    
	    return bean;
	}
}