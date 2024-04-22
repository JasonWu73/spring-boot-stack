package net.wuxianjie.web.controller;

import java.util.Map;

import net.wuxianjie.webkit.common.util.OSUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本控制器。
 */
@RestController
@RequestMapping("/api/v1")
public class VersionController {

    /**
     * 获取版本信息。
     *
     * @return 版本信息
     */
    @RequestMapping("/public/version")
    public Map<String, String> version() {
        return Map.of("version", "v1.0.0", "os", OSUtils.getOsName());
    }

}
