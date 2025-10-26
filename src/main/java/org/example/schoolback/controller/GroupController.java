package org.example.schoolback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.schoolback.dto.GroupDTO;
import org.example.schoolback.entity.Group;
import org.example.schoolback.service.GroupService;
import org.example.schoolback.util.assembler.impl.GroupAssembler;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<GroupDTO> createGroup(@RequestBody GroupDTO groupDTO) {
        final Group createdGroup = groupService.createGroup(groupDTO, resource -> groupAssembler.createEntity(groupDTO));
        final GroupDTO resource = groupAssembler.toModel(createdGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить группу")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @RequestBody GroupDTO groupDTO) {
        final Group updatedGroup = groupService.updateGroup(id, groupDTO, (groupEntity, resource) -> groupAssembler.updateEntity(groupEntity, resource));
        final GroupDTO resource = groupAssembler.toModel(updatedGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить группу")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
