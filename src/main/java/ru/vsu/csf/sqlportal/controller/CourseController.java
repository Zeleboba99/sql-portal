package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/courses/author/{user_id}")
    public Page<CourseResponse> getAllCoursesByAuthorId(@PathVariable("user_id") Long user_id,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam(value = "sort", defaultValue = "true") boolean sort) {
        return courseService.getCourses(user_id, page, size, sort);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/courses")
    public Page<CourseResponse> getAllCourses(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam(value = "sort", defaultValue = "true") boolean sort) {
        return courseService.getAllCourses(page, size, sort);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/courses/{course_id}")
    public CourseResponse getCourseById(@PathVariable("course_id") Long course_id) {
        return courseService.getCourseById(course_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/courses")
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        return courseService.createCourse(courseRequest);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/courses/{course_id}")
    public void deleteCourse(@PathVariable("course_id") Long course_id) {
        courseService.deleteCourse(course_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/courses/{course_id}")
    public CourseResponse updateCourse(@PathVariable("course_id") Long course_id, @Valid @RequestBody CourseRequest courseRequest) {
        return courseService.updateCourse(course_id, courseRequest);
    }

}
