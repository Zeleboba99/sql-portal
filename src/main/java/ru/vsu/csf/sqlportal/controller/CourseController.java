package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.sqlportal.dto.request.CourseRequest;
import ru.vsu.csf.sqlportal.dto.response.CourseResponse;
import ru.vsu.csf.sqlportal.service.CourseService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/courses/author/{user_id}")
    public List<CourseResponse> getAllCourses(@PathVariable("user_id") Long user_id) {
        return courseService.getCourses(user_id);
    }

    @GetMapping("/courses/{course_id}")
    public CourseResponse getCourseById(@PathVariable("course_id") Long course_id) {
        return courseService.getCourseById(course_id);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/courses")
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        return courseService.createCourse(courseRequest);
    }

    @DeleteMapping("/courses/{course_id}")
    public void deleteCourse(@PathVariable("course_id") Long course_id) {
        courseService.deleteCourse(course_id);
    }

}
