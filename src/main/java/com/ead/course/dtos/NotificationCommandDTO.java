package com.ead.course.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class NotificationCommandDTO {

    private String title;
    private String message;
    private UUID userId;
}