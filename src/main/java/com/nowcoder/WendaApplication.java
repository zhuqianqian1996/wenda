package com.nowcoder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;


@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.nowcoder"})
@MapperScan(basePackages = {"com.nowcoder.dao"})
public class WendaApplication
{
    public static void main( String[] args )
    {

        SpringApplication.run(WendaApplication.class,args);
    }
}

