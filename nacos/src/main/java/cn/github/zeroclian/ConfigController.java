package cn.github.zeroclian;

import cn.github.zeroclian.annotation.IgnoreResponseAdvice;
import cn.github.zeroclian.util.FileUtils;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @Desciption 配置控制器
 * @Author: ZeroClian
 * @Date: 2021-09-12 10:16 上午
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    private String name;

    @GetMapping("/get")
    @IgnoreResponseAdvice
    public String get() {
        try {
            String serverAddr = "127.0.0.1:8848";
            String dataId = "nacos-dev.yml";
            String group = "DEFAULT_GROUP";
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, 5000);
            System.out.println(content);
            return content;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return "false";
    }

    /**
     * 更新配置（不存在时创建配置）
     *
     * @param name
     * @return
     */
    @GetMapping("/update")
    public Boolean updateConfig(String name) {
        try {
            // 初始化配置服务，控制台通过示例代码自动获取下面参数
            String serverAddr = "127.0.0.1:8848";
            String dataId = "nacos-dev.yml";
            String group = "DEFAULT_GROUP";
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = FileUtils.file2String("");
            boolean isPublishOk = configService.publishConfig(dataId, group, content, ConfigType.YAML.getType());
            System.out.println(isPublishOk);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
