package com.hadoken.jpa.base;

import java.util.List;

/**
 * 对象转换
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/09 9:24
 */
public interface BaseMapper<D, E> {

    /**
     * DTO转Entity
     *
     * @param dto /
     * @return /
     */
    E toEntity(D dto);

    /**
     * Entity转DTO
     *
     * @param entity /
     * @return /
     */
    D toDto(E entity);

    /**
     * DTO集合转Entity集合
     *
     * @param dtoList /
     * @return /
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Entity集合转DTO集合
     *
     * @param entityList /
     * @return /
     */
    List<D> toDto(List<E> entityList);
}
