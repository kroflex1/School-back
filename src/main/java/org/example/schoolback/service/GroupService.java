package org.example.schoolback.service;

import org.apache.commons.lang3.StringUtils;
import org.example.schoolback.dto.GroupDTO;
import org.example.schoolback.entity.Group;
import org.example.schoolback.repository.GroupRepository;
import org.example.schoolback.util.Updater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public Group createGroup(GroupDTO groupDTO, Converter<GroupDTO, Group> converter) {
        final Group newGroup = converter.convert(groupDTO);
        validateCreate(newGroup);

        return groupRepository.save(newGroup);
    }

    public Group updateGroup(Long groupId, GroupDTO groupDTO, Updater<Group, GroupDTO> updater) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new IllegalArgumentException(String.format("Group with id: %d not found ", groupId));
        }

        final Group existGroup = group.get();
        updater.update(existGroup, groupDTO);
        validateUpdate(groupId, existGroup);

        return groupRepository.save(existGroup);
    }

    public void deleteGroup(Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new IllegalArgumentException(String.format("Group with id: %d not found ", groupId));
        }

        groupRepository.delete(group.get());
    }

    private void validateCreate(Group group) {
        validateCommonFields(group);

        if (!groupRepository.findByTeacherAndNameContainingIgnoreCase(group.getTeacher(), group.getName()).isEmpty()) {
            throw new IllegalArgumentException(String.format("Group with given teacher: %s and name: %s already exists",group.getTeacher().getFullName(), group.getName()));
        }
    }

    private void validateUpdate(Long groupId, Group updatedGroup) {
        final Group existGroup = groupRepository.findById(groupId).get();

        //проверяем, изменился ли учитель у группы или название группы, и если что-то изменилось, то смотрим нет ли уже такой группы
        if (!existGroup.getName().equals(updatedGroup.getName()) || !existGroup.getTeacher().equals(updatedGroup.getTeacher())) {
            if (!groupRepository.findByTeacherAndNameContainingIgnoreCase(updatedGroup.getTeacher(), updatedGroup.getName()).isEmpty()) {
                throw new IllegalArgumentException(String.format("Group with given teacher: %s and name: %s already exists", updatedGroup.getTeacher().getFullName(), updatedGroup.getName()));
            }
        }
    }


    private void validateCommonFields(Group group) {
        if (StringUtils.isBlank(group.getName())) {
            throw new IllegalArgumentException("Group name cannot be empty");
        }

        if (group.getTeacher() == null) {
            throw new IllegalArgumentException("Group cannot be withou teacher");
        }
    }
}
