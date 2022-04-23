package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ModuleController {

    private final CourseService courseService;

    private final ModuleService moduleService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                             @RequestBody @Valid ModuleDTO moduleDTO
    ) {

        Optional<CourseModel> optionalCourseModel = courseService.findById(courseId);

        return optionalCourseModel.<ResponseEntity<Object>>map(courseModel -> {

            ModuleModel moduleModel = new ModuleModel();

            BeanUtils.copyProperties(moduleDTO, moduleModel);
            moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
            moduleModel.setCourse(courseModel);

            return ResponseEntity.status(CREATED).body(moduleService.save(moduleModel));

        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Course not found!"));


    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);


        return optionalModuleModel.<ResponseEntity<Object>>map(moduleModel -> {
            moduleService.delete(moduleModel);
            return ResponseEntity.status(OK).body("Module Deleted with Successful");
        }).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Module Not Found!"));

    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @Valid @RequestBody ModuleDTO moduleDTO) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        return optionalModuleModel.<ResponseEntity<Object>>map(moduleModel -> {
                    moduleModel.setTitle(moduleDTO.getTitle());
                    moduleModel.setDescription(moduleDTO.getDescription());

                    return ResponseEntity.status(OK).body(moduleService.save(moduleModel));
                })
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Module Not Found!"));

    }

    @GetMapping("/courses/{courseId}/modules")
    ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(OK).body(moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    ResponseEntity<Object> getModuleModel(@PathVariable(value = "courseId") UUID courseId,
                                          @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        return optionalModuleModel.<ResponseEntity<Object>>map(moduleModel ->
                        ResponseEntity.status(OK).body(moduleModel))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("Module Not Found!"));
    }
}
