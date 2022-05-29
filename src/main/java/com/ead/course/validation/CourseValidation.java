package com.ead.course.validation;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CourseValidation implements Validator {

    private final Validator validator;

    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDTO courseDTO = (CourseDTO) o;

        validator.validate(courseDTO, errors);
        validateUserIstructor(courseDTO.getUserInstructor(), errors);

    }

    private void validateUserIstructor(UUID userId, Errors errors) {
        Optional<UserModel> userModelOpt = userService.findById(userId);
        userModelOpt.ifPresentOrElse(userModel -> {
            if (!userModel.getUserType().equals(UserType.INSTRUCTOR.name())) {
                errors.rejectValue("userInstructor", "userInstructorError", "User must be INSTRUCTOR or ADMIN.");
            }
        }, () -> errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not fount."));

    }
}
