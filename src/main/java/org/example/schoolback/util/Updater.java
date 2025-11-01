package org.example.schoolback.util;

/**
 * Интерфейс для обновления сущности
 */
@FunctionalInterface
public interface Updater<ENTITY, DTO> {

    /**
     * Обновляет существующую Entity данными из DTO/Model/resource
     * @param dto DTO с новыми данными
     * @param entity существующая Entity для обновления
     */
    void update(ENTITY entity, DTO dto);
}