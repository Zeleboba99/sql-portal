package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.request.CourseRequest;
import ru.vsu.csf.sqlportal.dto.response.CourseResponse;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Course;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.CourseRepository;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.ConverterService;
import ru.vsu.csf.sqlportal.service.CourseService;

import static ru.vsu.csf.sqlportal.service.ConverterService.convertToCourseResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<CourseResponse> getCourses(Long author_id, int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<Course> coursePage = courseRepository.findAllByAuthorId(author_id, PageRequest.of(page, size, sortOrder));
        long totalElements = coursePage.getTotalElements();
        List<CourseResponse> courseResponses = coursePage.stream().map(
                ConverterService::convertToCourseResponse
        ).collect(Collectors.toList());
        return new PageImpl<>(courseResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public Page<CourseResponse> getAllCourses(int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<Course> courseResponsePage = courseRepository.findAll(PageRequest.of(page, size, sortOrder));
        long totalElements = courseResponsePage.getTotalElements();
        List<CourseResponse> courseResponses = courseResponsePage.stream().map(
                ConverterService::convertToCourseResponse
        ).collect(Collectors.toList());
        return new PageImpl<>(courseResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public CourseResponse getCourseById(Long course_id) {
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        return convertToCourseResponse(course);
    }

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = new Course(courseRequest.getName(),
                courseRequest.getDescription(),
                author);

        Course newCourse = courseRepository.save(course);
        return convertToCourseResponse(course);
    }

    @Override
    public void deleteCourse(Long course_id) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        if (!user.getId().equals(course.getAuthor().getId())) {
            throw new AbuseRightsException(user.getLogin());
        }
        courseRepository.deleteById(course_id);
    }

    @Override
    public CourseResponse updateCourse(Long course_id, CourseRequest courseRequest) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        if (!user.getId().equals(course.getAuthor().getId())) {
            throw new AbuseRightsException(user.getLogin());
        }
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        Course updatedCourse = courseRepository.save(course);
        return convertToCourseResponse(course);
    }


    private User getCurrentUser() {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
    }
}
