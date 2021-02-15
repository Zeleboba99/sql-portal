package ru.vsu.csf.sqlportal.service;

import org.springframework.data.domain.Page;
import ru.vsu.csf.sqlportal.dto.request.CourseRequest;
import ru.vsu.csf.sqlportal.dto.response.CourseResponse;
import ru.vsu.csf.sqlportal.model.Course;

import java.util.List;

public interface CourseService {
    Page<CourseResponse> getCourses(Long author_id, int page, int size, boolean sort);
    Page<CourseResponse> getAllCourses(int page, int size, boolean sort);
    CourseResponse getCourseById(Long course_id);
    CourseResponse createCourse(CourseRequest courseRequest);
    void deleteCourse(Long course_id);
    CourseResponse updateCourse(Long course_id, CourseRequest courseRequest);
}
