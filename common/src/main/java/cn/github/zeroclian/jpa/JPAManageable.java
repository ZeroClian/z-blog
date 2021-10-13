package cn.github.zeroclian.jpa;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 可被管理类型的jpa实现
 *
 * @// TODO: 2021/9/3 添加实体操作监听
 * @Author: ZeroClian
 * @Date: 2021-09-03 9:14 上午
 */
@Data
@MappedSuperclass
public abstract class JPAManageable implements Manageable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11)
    private Integer id;

    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

}
