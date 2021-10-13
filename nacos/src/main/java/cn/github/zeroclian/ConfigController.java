package cn.github.zeroclian;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption 配置控制器
 * @Author: ZeroClian
 * @Date: 2021-09-12 10:16 上午
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value(value = "${author.name}")
    private String name;

    @GetMapping("/get")
    public String get() {
        System.out.println(name);
        return name;
    }
}
