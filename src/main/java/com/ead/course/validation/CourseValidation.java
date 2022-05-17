package com.ead.course.validation;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CourseValidation implements Validator {

    private final Validator validator;
    private final AuthUserClient authUserClient;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDTO courseDTO = (CourseDTO) o;

        validator.validate(courseDTO, errors);
        validateUserIstructor(courseDTO.getUserInstructor(),errors);

    }

    private void validateUserIstructor(UUID userId, Errors errors) {
        ResponseEntity<UserDTO> responseUserInstructor;

        try {
            responseUserInstructor = authUserClient.getUserById(userId);
            if (!responseUserInstructor.getBody().getUserType().equals(UserType.INSTRUCTOR)) {
                errors.rejectValue("userInstructor", "userInstructorError", "User must be INSTRUCTOR or ADMIN.");
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not fount.");
            }
        }
    }
}
