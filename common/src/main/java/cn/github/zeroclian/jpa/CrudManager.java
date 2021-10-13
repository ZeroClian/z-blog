package cn.github.zeroclian.jpa;

import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 提供基本的增删改查
 *
 * @Author: ZeroClian
 * @Date: 2021-09-03 9:19 上午
 */
public interface CrudManager<E extends JPAManageable, ID extends Serializable> {

    /**
     * 获取实体对应的Repository
     *
     * @return
     */
    CrudRepository<E, ID> getRepository();

    /**
     * 保存单个
     *
     * @param entity 待保存的实体
     * @return 保存完的实体
     */
    default E save(E entity) {
        return getRepository().save(entity);
    }

    /**
     * 保存多个
     *
     * @param entities 待保存的实体列表
     * @return 保存完的实体列表
     */
    default List<E> saveAll(Iterable<E> entities) {
        entities = getRepository().saveAll(entities);
        List<E> savedEntityList = new ArrayList<>();
        entities.forEach(savedEntityList::add);
        return savedEntityList;
    }

    /**
     * 更新单个
     *
     * @param id     待更新的实体ID
     * @param entity 待更新的实体
     * @return 更新完的实体
     */
    @Transactional(rollbackFor = Exception.class)
    default E update(ID id, E entity) {
        E oldEntity = findOne(id);
        if (Objects.isNull(oldEntity)) {
            throw new RuntimeException("找不到ID为：" + id + "的实体");
        }
        BeanUtils.copyProperties(entity, oldEntity, "id", "createTime", "updateTime");
        return getRepository().save(oldEntity);
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    default E findOne(ID id) {
        return getRepository().findById(id).orElse(null);
    }

    /**
     * 查询所有
     *
     * @return
     */
    default List<E> findAll() {
        List<E> entities = new ArrayList<>();
        getRepository().findAll().forEach(entities::add);
        return entities;
    }

    /**
     * 根据ID数组查询
     *
     * @param idList
     * @return
     */
    default List<E> findAllById(List<ID> idList) {
        List<E> entities = new ArrayList<>();
        getRepository().findAllById(idList).forEach(entities::add);
        return entities;
    }

    /**
     * 删除单个
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    default void delete(ID id) {
        getRepository().deleteById(id);
    }

    /**
     * 删除多个
     *
     * @param entities
     */
    @Transactional(rollbackFor = Exception.class)
    default void deleteAll(List<E> entities) {
        getRepository().deleteAll(entities);
    }
}
