package cn.github.zeroclian.enumeration;

/**
 * @Author: ZeroClian
 * @Date: 2021-09-03 5:31 下午
 */
public interface ResultStatus {

    /**
     * 获取状态码
     *
     * @return 状态码字符串
     */
    String getCode();

    /**
     * 获取描述
     *
     * @return 错误原因字符串
     */
    String getReason();

}
