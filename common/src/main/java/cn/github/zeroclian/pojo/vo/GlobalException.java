package cn.github.zeroclian.pojo.vo;

import cn.github.zeroclian.enumeration.CommonResultStatus;
import cn.github.zeroclian.enumeration.ResultStatus;

/**
 * @Desciption 自定义全局异常
 * @Author: ZeroClian
 * @Date: 2021-09-09 2:27 下午
 */
public class GlobalException extends RuntimeException {

    private final ResultStatus status;

    public GlobalException(String message) {
        this(CommonResultStatus.ERROR, message);
    }

    public GlobalException(ResultStatus status) {
        this(status, null);
    }

    public GlobalException(ResultStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }

}
