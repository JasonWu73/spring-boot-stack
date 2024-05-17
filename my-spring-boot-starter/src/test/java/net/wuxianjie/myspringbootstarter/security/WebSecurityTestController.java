package net.wuxianjie.myspringbootstarter.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-api/v1")
class WebSecurityTestController {

    @GetMapping("/public")
    String publicGetApi() {
        if (AuthUtils.getCurrentUser().isPresent()) {
            throw new RuntimeException("公共接口不应该有用户信息");
        }
        return "GET 公共接口可以访问";
    }

    @PostMapping("/public")
    String publicPostApi() {
        if (AuthUtils.getCurrentUser().isPresent()) {
            throw new RuntimeException("公共接口不应该有用户信息");
        }
        return "POST 公共接口可以访问";
    }

    @GetMapping("/root")
    @PreAuthorize("hasAuthority('root')")
    String root() {
        String username = AuthUtils.getCurrentUser().orElseThrow().username();
        return "超级管理员可以访问 - " + username;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    String admin() {
        String username = AuthUtils.getCurrentUser().orElseThrow().username();
        return "管理员可以访问 - " + username;
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user')")
    String user() {
        String username = AuthUtils.getCurrentUser().orElseThrow().username();
        return "用户可以访问 - " + username;
    }

    @GetMapping("/token")
    String getToken() {
        return "获取 GET TOKEN";
    }

    @PostMapping("/token")
    String postToken() {
        return "提交 POST TOKEN";
    }

    @PostMapping("/guest")
    String guest() {
        return "游客可以访问";
    }
}
