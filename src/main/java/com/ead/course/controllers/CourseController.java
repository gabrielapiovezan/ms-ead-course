package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@Valid @RequestBody CourseDTO courseDTO) {
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastTimeUpdate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(courseModel));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        Optional<CourseModel> optionalCourseModel = courseService.findById(courseId);
        if (!optionalCourseModel.isPresent()) {
            ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        courseService.delete(optionalCourseModel.get());
        return ResponseEntity.status(OK).body("Course Deleted with Successful");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable UUID courseId,
                                               @Valid @RequestBody CourseDTO courseDTO) {
        var optionalCourseModel = courseService.findById(courseId);
        if (!optionalCourseModel.isPresent()) {
            ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        var courseModel = optionalCourseModel.get();

        courseModel.setName(courseDTO.getName());
        courseModel.setDescription(courseDTO.getDescription());
        courseModel.setImageUrl(courseDTO.getImageURL());
        courseModel.setCourseStatus(courseDTO.getCourseStatus());
        courseModel.setCourseLevel(courseDTO.getCourseLevel());
        courseModel.setLastTimeUpdate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(OK).body(courseService.save(courseModel));

    }

    @GetMapping
    ResponseEntity<List<CourseModel>> getAllCourses() {
        return ResponseEntity.status(OK).body(courseService.findAll());
    }

    @GetMapping("/{courseId}")
    ResponseEntity<Object> getCourseModel(@PathVariable UUID courseId) {
        var optionalCourseModel = courseService.findById(courseId);
        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        return ResponseEntity.status(OK).body(optionalCourseModel.get());
    }
}
