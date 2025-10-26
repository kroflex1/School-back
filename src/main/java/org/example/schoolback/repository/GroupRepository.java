package org.example.schoolback.repository;

import org.example.schoolback.entity.Group;
import org.example.schoolback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTeacher(User teacher);

    List<Group> findByNameContainingIgnoreCase(String name);

    List<Group> findByTeacherAndNameContainingIgnoreCase(User teacher, String name);
}