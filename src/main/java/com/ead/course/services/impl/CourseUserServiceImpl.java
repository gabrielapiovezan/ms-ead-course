package com.ead.course.services.impl;

import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CourseUserServiceImpl implements CourseUserService {

    private final CourseUserRepository courseUserRepository;
}
