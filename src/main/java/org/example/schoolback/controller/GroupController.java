package org.example.schoolback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.schoolback.dto.GroupDTO;
import org.example.schoolback.dto.request.CreateGroupRequest;
import org.example.schoolback.entity.Group;
import org.example.schoolback.service.GroupService;
import org.example.schoolback.util.assembler.impl.GroupAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupAssembler groupAssembler;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Получить информацию о группе по id")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long id) {
        Optional<Group> group = groupService.getGroupById(id);
        if (group.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GroupDTO resource = groupAssembler.toModel(group.get());
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую группу")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        final Group createdGroup = groupService.createGroup(createGroupRequest, resource -> groupAssembler.createEntity(resource));
        final GroupDTO resource = groupAssembler.toModel(createdGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить группу")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @RequestBody GroupDTO groupDTO) {
        final Group updatedGroup = groupService.updateGroup(id, groupDTO, (groupEntity, resource) -> groupAssembler.updateEntity(groupEntity, resource));
        final GroupDTO resource = groupAssembler.toModel(updatedGroup);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить группу")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Получить группы, в которых состоит пользователь",
            description = """
                    Получить группы, в которых состоит пользователь.
                    
                    **Влияние роли:**
                    - ADMIN: получает информацию о всех группах;
                    - TEACHER: получает информацию о группах, в которых он является преподавателем;
                    - STUDENT: получает информацию о группах, в которых он состоит;
                    """)
    public Page<GroupDTO> getAvailableGroups(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        final Page<Group> availableGroups = groupService.getAvailableGroupsForCurrentUser(pageable);
        return availableGroups.map(x -> groupAssembler.toModel(x));
    }

    @PostMapping("{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить ученика в группу")
    public ResponseEntity<GroupDTO> addStudentToGroup(@PathVariable Long groupId, @RequestParam Long studentId) {
        final Group group = groupService.addStudentToGroup(groupId, studentId);
        final GroupDTO resource = groupAssembler.toModel(group);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить ученика из группы")
    public ResponseEntity<GroupDTO> removeStudentFromGroup(@PathVariable Long groupId, @RequestParam Long studentId) {
        final Group group = groupService.removeStudentFromGroup(groupId, studentId);
        final GroupDTO resource = groupAssembler.toModel(group);
        return ResponseEntity.ok(resource);
    }
}
