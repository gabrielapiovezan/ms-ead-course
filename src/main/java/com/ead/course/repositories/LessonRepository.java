package com.ead.course.repositories;

import com.ead.course.models.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<LessonModel, UUID> {

    @Query(value = "SELECT * tb_lessons WHERE module_module_id :moduleId", nativeQuery = true)
    Optional<List<LessonModel>> findAllLessonsIntoModules(@Param("moduleId") UUID moduleId);
}
