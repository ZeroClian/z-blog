package cn.github.zeroclian.pojo.vo;

import cn.github.zeroclian.enumeration.CommonResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应对象定义
 *
 * @author ZeroClian
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {

    private String code;
    private String message;
    private T data;

    public CommonResponse(String code, String message) {

        this.code = code;
        this.message = message;
    }

    public CommonResponse(T data) {
        this.data = data;
        this.code = CommonResultStatus.SUCCESS.getCode();
        this.message = CommonResultStatus.SUCCESS.getReason();
    }
}
