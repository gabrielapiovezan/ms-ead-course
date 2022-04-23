package com.ead.course.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class LessonDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String videoURL;
}
