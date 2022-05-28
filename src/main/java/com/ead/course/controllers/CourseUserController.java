package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDTO;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CourseUserController {

    private final CourseService courseService;

    private final UserService userService;

    @GetMapping("courses/{courseId}/users")
    public ResponseEntity<Object> getAllCoursesByUser(SpecificationTemplate.UserSpec spec,
                                                      @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable,
                                                      @PathVariable UUID courseId) {

        var optionalCourseModel = courseService.findById(courseId);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        return ResponseEntity.status(OK).body(userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));

    }

    @PostMapping("courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody SubscriptionDTO subscriptionDTO) {

        ResponseEntity<Object> responseUser;
        var optionalCourseModel = courseService.findById(courseId);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        //todo: verificacoes state transfer
        return ResponseEntity.status(CREATED).body("");
    }

}