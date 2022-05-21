package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseUserRepository extends JpaRepository<CourseUserModel, UUID> {

    @Query(value = "select * from tb_courses_users where course_course_id = :courseId", nativeQuery = true)
    List<CourseUserModel> findAllUsersIntoCourse(@Param("courseId") String courseId);
    boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId);

    boolean existsByUserId(UUID UserId);

    void deleteAllByUserId(UUID userId);
}
