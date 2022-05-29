package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel> {

    @Query(value = "select * from tb_courses_users where course_id = :courseId and user_id = :userId", nativeQuery = true)
    List<CourseModel> existsByCourseAndUser(@Param("courseId") String courseId, @Param("userId") String userId);

    @Query(value = "insert into tb_courses_users (course_id,user_id) values (:courseId,:userId)", nativeQuery = true)
    @Modifying
    void saveSubscriptionCourseAndUser(@Param("courseId") String courseId, @Param("userId") String userId);
}
