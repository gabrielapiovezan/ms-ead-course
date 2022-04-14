package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId())
                .ifPresent(moduleModels -> {
                    moduleModels.forEach(moduleModel -> lessonRepository
                            .findAllLessonsIntoModules(moduleModel.getModuleId())
                            .ifPresent(lessonRepository::deleteAll));
                    moduleRepository.deleteAll(moduleModels);
                });
        courseRepository.delete(courseModel);
    }


}

