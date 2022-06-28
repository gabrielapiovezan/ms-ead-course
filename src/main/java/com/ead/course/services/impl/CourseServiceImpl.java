package com.ead.course.services.impl;

import com.ead.course.dtos.NotificationCommandDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import com.ead.course.publishers.NotificationCommandPublisher;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final LessonRepository lessonRepository;

    private final UserRepository userRepository;

    private final NotificationCommandPublisher notificationCommandPublisher;


    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        AtomicBoolean verifyCourseUser = new AtomicBoolean(false);

        List<ModuleModel> moduleModels = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId().toString());

        if (!moduleModels.isEmpty()) {
            moduleModels.forEach(moduleModel -> {
                List<LessonModel> lessonModels = lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId().toString());
                if (!lessonModels.isEmpty()) {
                    lessonRepository.deleteAll(lessonModels);
                    verifyCourseUser.set(true);
                }
                moduleRepository.deleteAll(moduleModels);
            });
        }
        courseRepository.deleteCourseUserByCourseId(courseModel.getCourseId().toString());
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
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }

    @Override
    public boolean existsByCourseAndUser(UUID courseId, UUID userId) {
        return !courseRepository.existsByCourseAndUser(courseId.toString(), userId.toString()).isEmpty();
    }

    @Override
    @Transactional
    public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
        courseRepository.saveSubscriptionCourseAndUser(courseId.toString(), userId.toString());
    }

    @Override
    @Transactional
    public void saveSubscriptionUserInCourse(CourseModel courseModel, UserModel userModel) {
        courseRepository.saveSubscriptionCourseAndUser(courseModel.getCourseId().toString(), userModel.getUserId().toString());

        try {
           var notificationCommandDTO =
                   NotificationCommandDTO.builder()
                           .title("Bem vindo ao curso(a): ".concat(courseModel.getName()))
                           .message("a sua inscrição foi realizada com sucesso!")
                           .userId(userModel.getUserId())
                   .build();
            notificationCommandPublisher.publishNotificationCommand(notificationCommandDTO);

        }catch (Exception e){
            log.warn("Error sending notification");
        }
    }


}

