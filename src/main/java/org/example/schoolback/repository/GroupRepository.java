package org.example.schoolback.repository;

import org.example.schoolback.entity.Group;
import org.example.schoolback.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTeacherAndNameContainingIgnoreCase(User teacher, String name);

    List<Group> findByTeacherId(Long teacherId);

    @Query(value = "SELECT g.* FROM groups g " +
            "JOIN participants_groups pg ON g.id = pg.group_id " +
            "WHERE pg.user_id = :userId",
            nativeQuery = true)
    List<Group> findAllGroupsByStudentId(@Param("userId") Long studentId);

    Page<Group> findByTeacherId(Long teacherId, Pageable pageable);

    @Query(value = "SELECT g.* FROM groups g " +
            "JOIN participants_groups pg ON g.id = pg.group_id " +
            "WHERE pg.user_id = :userId",
            nativeQuery = true)
    Page<Group> findAllGroupsByStudentId(@Param("userId") Long studentId, Pageable pageable);
}