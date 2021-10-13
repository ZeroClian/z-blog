package cn.github.zeroclian.jpa;

import java.io.Serializable;

/**
 * @Author: ZeroClian
 * @Date: 2021-09-03 9:13 上午
 */
public interface Manageable<I> extends Serializable {

    I getId();

    void setId(I id);
}
