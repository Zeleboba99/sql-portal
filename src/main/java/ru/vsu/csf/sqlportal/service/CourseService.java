package ru.vsu.csf.sqlportal.service;

import ru.vsu.csf.sqlportal.dto.request.CourseRequest;
import ru.vsu.csf.sqlportal.dto.response.CourseResponse;
import ru.vsu.csf.sqlportal.model.Course;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getCourses(Long author_id);
    List<CourseResponse> getAllCourses();
    CourseResponse getCourseById(Long course_id);
    CourseResponse createCourse(CourseRequest courseRequest);
    void deleteCourse(Long course_id);
}
