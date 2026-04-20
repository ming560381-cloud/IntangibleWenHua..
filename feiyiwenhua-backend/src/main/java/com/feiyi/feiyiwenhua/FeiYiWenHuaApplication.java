package com.feiyi.feiyiwenhua;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.feiyi.feiyiwenhua.repository")
public class FeiYiWenHuaApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeiYiWenHuaApplication.class, args);
    }
}
