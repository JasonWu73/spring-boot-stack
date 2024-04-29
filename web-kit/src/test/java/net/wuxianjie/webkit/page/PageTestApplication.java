package net.wuxianjie.webkit.page;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.wuxianjie.webkit.security.SecurityConfig;

@SpringBootApplication(exclude = SecurityConfig.class)
class PageTestApplication {
}