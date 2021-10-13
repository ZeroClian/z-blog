package cn.github.zeroclian.advice;

import cn.github.zeroclian.annotation.IgnoreResponseAdvice;
import cn.github.zeroclian.enumeration.CommonResultStatus;
import cn.github.zeroclian.pojo.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一数据响应增强
 *
 * @Author: ZeroClian
 * @Date: 2021-09-09 1:52 下午
 */
@RestControllerAdvice
public class CommonResponseAdvice implements ResponseBodyAdvice {

    /**
     * 判断是否需要对响应进行处理
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 如果当前方法所在的类标识了 @IgnoreResponseAdvice 注解, 不需要处理
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        // 如果当前方法标识了 @IgnoreResponseAdvice 注解, 不需要处理
        return !methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class);
    }

    /**
     * 响应返回之前的处理
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        CommonResponse<Object> response = new CommonResponse<>(CommonResultStatus.SUCCESS.getCode(), CommonResultStatus.SUCCESS.getReason());
        if (o == null) {
            return response;
        } else if (o instanceof CommonResponse) {
            response = (CommonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
