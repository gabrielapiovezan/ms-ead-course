package com.ead.course.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService{

    public String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable) {

        return "/users?courseId="+courseId+"&page="+pageable.getPageNumber()+"&size="+pageable.getPageSize()+"&sort="+pageable.getSort().toString().replaceAll(": ", ",");

    }
}