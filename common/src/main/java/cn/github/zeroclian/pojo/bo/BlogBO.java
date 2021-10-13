package cn.github.zeroclian.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desciption
 * @Author: ZeroClian
 * @Date: 2021-09-12 4:39 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogBO {

    /**
     * 博客名
     */
    private String name;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 配置内容
     */
    private boolean useLocalCache;
}
