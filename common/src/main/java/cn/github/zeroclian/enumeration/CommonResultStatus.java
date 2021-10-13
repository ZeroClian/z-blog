package cn.github.zeroclian.enumeration;

import lombok.AllArgsConstructor;

/**
 * 通用的错误码枚举
 *
 * @Author: ZeroClian
 * @Date: 2021-09-03 5:29 下午
 */
@AllArgsConstructor
public enum CommonResultStatus implements ResultStatus {

    SUCCESS("00", "成功"),
    ERROR("01", "系统繁忙"),

    PARAM_ERROR("02", "参数格式错误"),
    LOGIN_EXPIRED("03", "登录已过期,请重新登陆"),

    /**
     * 一般用于根据ID查询不到对应的记录时
     */
    NO_RECORDS_FOUND("04", "找不到指定记录"),

    /**
     * 不支持的操作
     * 当接口传入的操作类型不是期望的类型时可以使用
     */
    UNSUPPORTED_OPERATION("05", "不支持的操作"),

    /**
     * 内部服务调用出错
     */
    INTERNAL_SERVICE_CALL_FAILED("06", "内部服务调用出错"),

    BAD_REQUEST("40", "Bad Request"),
    UNAUTHORIZED("41", "Unauthorized"),
    FORBIDDEN("43", "您没有访问权限"),
    NOT_FOUND("44", "Not Found"),

    REPETITIVE_OPERATION("45", "请勿重复操作"),
    REPETITIVE_DATA("46", "重复数据"),

    Data_IS_NOT_EXPECTED("47", "数据达不到期望"),
    OPERATION_IS_NOT_EXPECTED("48", "操作达不到期望"),
    OPERATION_NOT_ALLOWED("49", "不允许的操作"),

    ;

    private final String code;
    private final String reason;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getReason() {
        return reason;
    }
}
