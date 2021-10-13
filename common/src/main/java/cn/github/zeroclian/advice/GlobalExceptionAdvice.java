package cn.github.zeroclian.advice;

import cn.github.zeroclian.pojo.vo.CommonResponse;
import cn.github.zeroclian.pojo.vo.GlobalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @Desciption 全局异常处理器
 * @Author: ZeroClian
 * @Date: 2021-09-09 2:34 下午
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(GlobalException.class)
    public CommonResponse<Object> handleGlobalException(GlobalException ge) {
        CommonResponse<Object> response = new CommonResponse<>(ge.getStatus().getCode(),
                Objects.isNull(ge.getMessage()) ? ge.getStatus().getReason() : ge.getMessage());
        return response;
    }

}
