package com.ead.course.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class CourseUserDTO {
    UUID userId;
    UUID courseId;
}
