package com.example.devices.service.mapper;

import java.util.Set;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - domain type parameter.
 */
public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    Set<D> toDtos(Set<E> entities);

    Set<E> toEntities(Set<D> dtos);
}
