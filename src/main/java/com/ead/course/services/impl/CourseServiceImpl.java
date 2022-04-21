package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        List<ModuleModel> moduleModels = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId().toString());

        if (!moduleModels.isEmpty()) {
            moduleModels.forEach(moduleModel -> {
                List<LessonModel> lessonModels = lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId().toString());
                if (!lessonModels.isEmpty()) {
                    lessonRepository.deleteAll(lessonModels);
                }
                moduleRepository.deleteAll(moduleModels);
            });
        }
        courseRepository.delete(courseModel);


    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public List<CourseModel> findAll() {
        return courseRepository.findAll();
    }


}

