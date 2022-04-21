package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found!");
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDTO, moduleModel);

        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(optionalCourseModel.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));

    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (optionalModuleModel.isEmpty()) {
            ResponseEntity.status(NOT_FOUND).body("Module Not Found!");
        }
        moduleService.delete(optionalModuleModel.get());
        return ResponseEntity.status(OK).body("Module Deleted with Successful");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @Valid @RequestBody ModuleDTO moduleDTO) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!optionalModuleModel.isPresent()) {
            ResponseEntity.status(NOT_FOUND).body("Module Not Found!");
        }
        var moduleModel = optionalModuleModel.get();
        moduleModel.setTitle(moduleDTO.getTitle());
        moduleModel.setDescription(moduleDTO.getDescription());

        return ResponseEntity.status(OK).body(moduleService.save(moduleModel));

    }

    @GetMapping("/courses/{courseId}/modules")
    ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(OK).body(moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    ResponseEntity<Object> getModuleModel(@PathVariable(value = "courseId") UUID courseId,
                                          @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!optionalModuleModel.isPresent()) {
            return ResponseEntity.status(NOT_FOUND).body("Module Not Found!");
        }
        return ResponseEntity.status(OK).body(optionalModuleModel.get());
    }
}
