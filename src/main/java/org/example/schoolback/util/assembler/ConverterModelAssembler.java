package org.example.schoolback.util.assembler;

public abstract class ConverterModelAssembler <Entity, Model> {

    /**
     * Преобразует Entity в Model/DTO
     */
    public abstract Model toModel(Entity entity);

    /**
     * Преобразует Model/DTO в новую Entity
     * Используется при создании новых объектов
     */
    public abstract Entity createEntity(Model model);


    /**
     * Обновляет существующую Entity данными из Model/DTO
     * Используется для обновления Entity
     */
    public abstract void updateEntity(Entity existingEntity, Model resource);
}
