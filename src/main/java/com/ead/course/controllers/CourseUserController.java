package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CourseUserController {

    private final AuthUserClient authUserClient;
    private final CourseService courseService;
    private final CourseUserService courseUserService;

    @GetMapping("courses/{courseId}/users")
    public ResponseEntity<Page<UserDTO>> getAllCoursesByUser(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @PathVariable UUID courseId) {
        return ResponseEntity.status(OK).body(authUserClient.getAllCoursesByUser(courseId, pageable));

    }

    @PostMapping("courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody SubscriptionDTO subscriptionDTO) {

        ResponseEntity<UserDTO> responseUser;
        var optionalCourseModel = courseService.findById(courseId);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body("Course Not Found!");
        }
        boolean existsByCourseAndUserId = courseUserService.existsByCourseAndUserId(optionalCourseModel.get(), subscriptionDTO.getUserId());

        if (existsByCourseAndUserId) {
            return ResponseEntity.status(CONFLICT).body("Error! subscripton already exists!");
        }
        try {
            responseUser = authUserClient.getUserById(subscriptionDTO.getUserId());
            if (responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED)) {
                return ResponseEntity.status(CONFLICT).body("User is blocked.");
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(NOT_FOUND)) {
                return ResponseEntity.status(NOT_FOUND).body("User Not Found.");
            }
        }
        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(optionalCourseModel.get().convertToCourseUserModel(subscriptionDTO.getUserId()));

        return ResponseEntity.status(CREATED).body(courseUserModel);
    }
}