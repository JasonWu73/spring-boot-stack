package net.wuxianjie.myspringbootstarter.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-api/v1/")
class WebSecurityTestController {

    @GetMapping("/public")
    String publicApi() {
        if (AuthUtils.getCurrentUser().isPresent()) {
            throw new RuntimeException("公共接口不应该有用户信息");
        }
        return "公共接口可以访问";
    }

    @GetMapping("/root")
    @PreAuthorize("hasAuthority('root')")
    String root() {
        return "超级管理员可以访问 - " + AuthUtils.getCurrentUser().orElseThrow().username();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    String admin() {
        return "管理员可以访问 - " + AuthUtils.getCurrentUser().orElseThrow().username();
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user')")
    String user() {
        return "用户可以访问 - " + AuthUtils.getCurrentUser().orElseThrow().username();
    }
}
