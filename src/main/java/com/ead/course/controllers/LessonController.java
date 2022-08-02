package com.ead.course.controllers;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    private final ModuleService moduleService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/modules/{moduleId}/lessons")
    ResponseEntity<Object> saveLesson(@PathVariable("moduleId") UUID moduleId,
                                      @RequestBody @Valid LessonDTO lessonDTO) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findById(moduleId);

        return optionalModuleModel.<ResponseEntity<Object>>map(moduleModel -> {
            LessonModel lessonModel = new LessonModel();
            BeanUtils.copyProperties(lessonDTO, lessonModel);
            lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
            lessonModel.setModule(moduleModel);
            return ResponseEntity.status(CREATED).body(lessonService.save(lessonModel));
        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Module not found!"));

    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    ResponseEntity<Object> deleteLesson(@PathVariable("moduleId") UUID moduleId,
                                        @PathVariable("lessonId") UUID lessonId) {
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);
        return optionalLessonModel.<ResponseEntity<Object>>map(lessonModel -> {
            lessonService.delete(lessonModel);
            return ResponseEntity.status(OK).body("Lesson deleted with success!");
        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Lesson not found!"));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    ResponseEntity<Object> updateLesson(@PathVariable("moduleId") UUID moduleId,
                                        @PathVariable("lessonId") UUID lessonId,
                                        @RequestBody @Valid LessonDTO lessonDTO) {


        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);
        return optionalLessonModel.<ResponseEntity<Object>>map(lessonModel -> {
            BeanUtils.copyProperties(lessonDTO, lessonModel);
            return ResponseEntity.status(OK).body(lessonService.save(lessonModel));
        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Lesson not found!"));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/modules/{moduleId}/lessons")
    ResponseEntity<Page<LessonModel>> getAllLessons(@PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.DESC) Pageable pageable,
                                                    SpecificationTemplate.LessonSpec spec,
                                                    @PathVariable("moduleId") UUID moduleId) {
        return ResponseEntity.status(OK).body(lessonService.findAllLessonsintoModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    ResponseEntity<Object> getLessonModule(@PathVariable("moduleId") UUID moduleId,
                                           @PathVariable("lessonId") UUID lessonId) {
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        return optionalLessonModel.<ResponseEntity<Object>>map(lessonModel ->
                        ResponseEntity.status(OK).body(lessonService.findAllLessonsintoModule(moduleId)))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Lesson not found!"));


    }
}
