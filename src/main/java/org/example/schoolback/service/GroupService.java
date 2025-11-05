package org.example.schoolback.service;

import org.apache.commons.lang3.StringUtils;
import org.example.schoolback.dto.GroupDTO;
import org.example.schoolback.dto.request.CreateGroupRequest;
import org.example.schoolback.entity.Group;
import org.example.schoolback.entity.Role;
import org.example.schoolback.entity.User;
import org.example.schoolback.repository.GroupRepository;
import org.example.schoolback.util.Updater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public Group createGroup(CreateGroupRequest createGroupRequest, Converter<GroupDTO, Group> converter) {
        final GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupName(createGroupRequest.getGroupName());
        groupDTO.setTeacherId(createGroupRequest.getTeacherId());

        final Group newGroup = converter.convert(groupDTO);
        validateCreate(newGroup);

        return groupRepository.save(newGroup);
    }

    /**
     * Возвращает список групп, доступных для текущего пользователя в зависимости от его роли.
     *
     * <p><b>Логика работы в зависимости от роли:</b>
     * <ul>
     *   <li><b>ADMIN</b> - получает полный список всех групп в системе</li>
     *   <li><b>TEACHER</b> - получает группы, в которых пользователь является преподавателем</li>
     *   <li><b>STUDENT</b> - получает группы, в которых пользователь состоит как студент</li>
     * </ul>
     */
    public List<Group> getAvailableGroupsForCurrentUser() {
        final User user = userService.getCurrentUser().get();
        if (user.getRole().equals(Role.ADMIN)) {
            return groupRepository.findAll();
        } else if (user.getRole().equals(Role.TEACHER)) {
            return groupRepository.findByTeacherId(user.getId());
        } else {
            return groupRepository.findAllGroupsByStudentId(user.getId());
        }
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

    public Group addStudentToGroup(Long groupId, Long studentId) {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<User> student = userService.getUserById(studentId);
        if (group.isEmpty()) {
            throw new IllegalArgumentException(String.format("Group with id: %d not found ", groupId));
        }
        if (student.isEmpty()) {
            throw new IllegalArgumentException(String.format("Student with id: %d not found ", studentId));
        }

        final Group existGroup = group.get();
        final User existStudent = student.get();
        if (existGroup.getParticipants().contains(existStudent)) {
            throw new IllegalArgumentException(String.format("Student with id: %d already a member of group with id %s ", studentId, groupId));
        }

        existGroup.getParticipants().add(existStudent);
        return groupRepository.save(existGroup);
    }


    public Group removeStudentFromGroup(Long groupId, Long studentId) {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<User> student = userService.getUserById(studentId);
        if (group.isEmpty()) {
            throw new IllegalArgumentException(String.format("Group with id: %d not found ", groupId));
        }
        if (student.isEmpty()) {
            throw new IllegalArgumentException(String.format("Student with id: %d not found ", studentId));
        }

        final Group existGroup = group.get();
        final User existStudent = student.get();
        existGroup.getParticipants().remove(existStudent);
        return groupRepository.save(existGroup);
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
