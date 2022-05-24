package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validation.CourseValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final CourseValidation courseValidation;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@Valid @RequestBody CourseDTO courseDTO, Errors errors) {
        log.debug("POST saveCourse courseDTO received {}", courseDTO.toString());

        courseValidation.validate(courseDTO, errors);

        if(errors.hasErrors()){
            return ResponseEntity.status(BAD_REQUEST).body(errors.getAllErrors());
        }

        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastTimeUpdate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(courseModel));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        Optional<CourseModel> optionalCourseModel = courseService.findById(courseId);

        return optionalCourseModel.<ResponseEntity<Object>>map(courseModel -> {
                    courseService.delete(courseModel);
                    return ResponseEntity.status(OK).body("Course Deleted with Successful");
                })
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Course Not Found!"));

    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable UUID courseId,
                                               @Valid @RequestBody CourseDTO courseDTO) {
        var optionalCourseModel = courseService.findById(courseId);

        return optionalCourseModel.<ResponseEntity<Object>>map(courseModel -> {
            BeanUtils.copyProperties(courseDTO, courseModel);
            courseModel.setLastTimeUpdate(LocalDateTime.now(ZoneId.of("UTC")));

            return ResponseEntity.status(OK).body(courseService.save(courseModel));
        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Course Not Found!"));


    }

    @GetMapping
    ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                    @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam(required = false) UUID userId) {

        return ResponseEntity.status(OK).body(courseService.findAll(spec, pageable));
    }

    @GetMapping("/{courseId}")
    ResponseEntity<Object> getCourseModel(@PathVariable UUID courseId) {
        var optionalCourseModel = courseService.findById(courseId);
        return optionalCourseModel.<ResponseEntity<Object>>map(courseModel -> ResponseEntity.status(OK).body(courseModel))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Course Not Found!"));

    }
}
